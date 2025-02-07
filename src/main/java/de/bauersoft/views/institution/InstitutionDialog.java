package de.bauersoft.views.institution;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.dom.Style;
import de.bauersoft.components.Multiplier;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.data.entities.institution.InstitutionMultiplier;
import de.bauersoft.data.entities.institution.InstitutionMultiplierKey;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.providers.AddressDataProvider;
import de.bauersoft.data.providers.InstitutionDataProvider;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.field.FieldMultiplierRepository;
import de.bauersoft.data.repositories.institution.InstitutionMultiplierRepository;
import de.bauersoft.services.*;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.address.AddressComboBox;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InstitutionDialog extends Dialog
{
	private InstitutionService institutionService;
	private InstitutionFieldsService institutionFieldsService;
	private AddressService addressService;
	private FieldService fieldService;
	private UserService userService;
	private InstitutionMultiplierService institutionMultiplierService;
	private InstitutionMultiplierRepository institutionMultiplierRepository;
	private CourseService courseService;
	private CourseRepository courseRepository;
	private FieldMultiplierService fieldMultiplierService;
	private FieldMultiplierRepository fieldMultiplierRepository;

	private InstitutionDataProvider institutionDataProvider;
	private AddressDataProvider addressDataProvider;

	private Institution item;

	private DialogState dialogState;

	private FormLayout inputLayout;
	private TextField nameTextField;
	private TextArea descriptionTextArea;
	private TextField customerIdTextField;
	private HorizontalLayout datePickerLayout;
	private TimePicker orderStartTimePicker;
	private TimePicker orderEndTimePicker;
	private AddressComboBox addressComboBox;
	private MultiSelectComboBox<User> userMultiSelectComboBox;
	private FieldComponent fieldComponent;

	private final LinkedHashMap<Field, LinkedHashMap<InstitutionMultiplier, NumberField>> instMultiplierMap;
	private final Set<Long> deletedFieldIds;

	private final AtomicReference<Field> selected;

	private final Consumer<Field> onSelectedFieldChange;
	private final HorizontalLayout multipliersControlLayout;
	private final FlexLayout multipliersInputLayout;
	private final Checkbox multipliersCheckBox;
	private final ComboBox<Field> fieldsComboBox;

	public InstitutionDialog(InstitutionService institutionService, InstitutionFieldsService institutionFieldsService, AddressService addressService, FieldService fieldService, UserService userService, InstitutionMultiplierService institutionMultiplierService, CourseService courseService, FieldMultiplierService fieldMultiplierService, InstitutionDataProvider institutionDataProvider, AddressDataProvider addressDataProvider, Institution item, DialogState dialogState)
	{
		this.institutionService = institutionService;
		this.institutionFieldsService = institutionFieldsService;
		this.addressService = addressService;
		this.fieldService = fieldService;
		this.userService = userService;
        this.institutionMultiplierService = institutionMultiplierService;
		this.institutionMultiplierRepository = institutionMultiplierService.getRepository();
        this.courseService = courseService;
		this.courseRepository = courseService.getRepository();
        this.fieldMultiplierService = fieldMultiplierService;
		this.fieldMultiplierRepository = fieldMultiplierService.getRepository();
        this.institutionDataProvider = institutionDataProvider;
        this.addressDataProvider = addressDataProvider;
        this.item = item;
		this.dialogState = dialogState;

		instMultiplierMap = new LinkedHashMap<>();
		deletedFieldIds = new HashSet<Long>();

		selected = new AtomicReference<Field>();

		this.setHeaderTitle(dialogState.toString());

		Binder<Institution> binder = new Binder<>(Institution.class);

		inputLayout = new FormLayout();
		inputLayout.setWidth("50vw");
		inputLayout.setMaxWidth("50em");
		//inputLayout.setHeight("50vh");
		//inputLayout.setMaxHeight("20em");

		inputLayout.setResponsiveSteps(new ResponsiveStep("0", 1));

		nameTextField = new TextField();
		nameTextField.setMaxLength(64);
		nameTextField.setRequired(true);
		nameTextField.setMinWidth("20em");

		descriptionTextArea = new TextArea();
		descriptionTextArea.setMaxLength(512);
		descriptionTextArea.setSizeFull();
		descriptionTextArea.setMinHeight("calc(4* var(--lumo-text-field-size))");

		customerIdTextField = new TextField();
		customerIdTextField.setWidthFull();
		customerIdTextField.setMaxLength(255);

		datePickerLayout = new HorizontalLayout();

		Span orderStartSpan = new Span("Von ");
		orderStartSpan.getStyle().setAlignSelf(Style.AlignSelf.CENTER);

		orderStartTimePicker = new TimePicker();
		orderStartTimePicker.setValue(LocalTime.of(0, 0));

		Span orderEndSpan = new Span(" bis ");
		orderEndSpan.getStyle().setAlignSelf(Style.AlignSelf.CENTER);

		orderEndTimePicker = new TimePicker();
		orderEndTimePicker.setValue(LocalTime.of(8, 0));

		datePickerLayout.add(orderStartSpan, orderStartTimePicker, orderEndSpan, orderEndTimePicker);

		addressComboBox = new AddressComboBox(addressService, addressDataProvider);
		addressComboBox.setRequired(true);
		addressComboBox.setItemLabelGenerator(address ->
				{
					return address.getStreet() + " " + address.getNumber() + " " + address.getPostal() + " " + address.getCity();
				});
		addressComboBox.setItems(addressService.getRepository().findAll());
		addressComboBox.setWidthFull();

		userMultiSelectComboBox = new MultiSelectComboBox<User>();
		userMultiSelectComboBox.setItemLabelGenerator(user ->
				{
					return user.getName() + " " + user.getSurname() + " [" + user.getEmail() + "]";
				});
		userMultiSelectComboBox.setItems(userService.getRepository().findAll());
		userMultiSelectComboBox.setWidthFull();

		fieldComponent = new FieldComponent();
		fieldComponent.setInstitutionFields(institutionFieldsService.getRepository().findAllByInstitutionId(item.getId()));
		fieldComponent.setFields(fieldService.getRepository().findAll());
		fieldComponent.updateView();
		fieldComponent.getStyle().setMarginTop("5px");
		fieldComponent.setHeight("50vh");

		inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "name"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(descriptionTextArea, "description"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(customerIdTextField, "customer id"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(datePickerLayout, "Bestellung"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(addressComboBox, "address"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(userMultiSelectComboBox, "user"), 1);

		binder.forField(nameTextField).asRequired((value, context) ->
		{
			return (value != null && !value.isBlank())
					? ValidationResult.ok()
					: ValidationResult.error("Name is required");
		}).bind("name");

		binder.bind(descriptionTextArea, "description");
		binder.bind(customerIdTextField, "customerId");

		binder.forField(orderStartTimePicker).withValidator((value, context) ->
		{
			return (value.isBefore(orderEndTimePicker.getValue())) ?
					ValidationResult.ok() :
					ValidationResult.error("Startzeit muss vor Endzeit liegen!");

		}).bind("orderStart");

		binder.forField(orderEndTimePicker).withValidator((value, context) ->
		{
			return (value.isAfter(orderStartTimePicker.getValue())) ?
					ValidationResult.ok() :
					ValidationResult.error("Endzeit muss nach Startzeit liegen!");

		}).bind("orderEnd");

		binder.bind(addressComboBox, "address");
		binder.bind(userMultiSelectComboBox, "users");
		binder.setBean(item);


		multipliersControlLayout = new HorizontalLayout();
		multipliersControlLayout.setWidth("50vw");
		multipliersControlLayout.setMaxWidth("50em");
		multipliersControlLayout.setHeight("1vh");
		multipliersControlLayout.setMaxHeight("1em");
		multipliersControlLayout.setAlignItems(FlexComponent.Alignment.CENTER);
		multipliersControlLayout.getStyle().set("margin-top", "30px");

		multipliersInputLayout = new FlexLayout();
		multipliersInputLayout.setVisible(false);
		multipliersInputLayout.setWidth("50vw");
		multipliersInputLayout.setMaxWidth("50em");
		multipliersInputLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
		multipliersInputLayout.getStyle().set("margin-top", "15px");

		multipliersCheckBox = new Checkbox("Lokale Multiplikatoren");
		multipliersCheckBox.setValue(item.useLocalMultiplier());

		fieldsComboBox = new ComboBox<>();
		fieldsComboBox.setVisible(item.useLocalMultiplier());
		fieldsComboBox.setItemLabelGenerator(field ->
		{
			return field.getName();
		});

		multipliersControlLayout.add(multipliersCheckBox, fieldsComboBox);

		fieldComponent.setOnEntriesChange(() ->
		{
			Set<Field> fields = getGridFields();

			fieldsComboBox.setItems(fields);

			fields.forEach(field ->
			{
				initInstitutionMultiplier(field);
				deletedFieldIds.remove(field.getId());
			});

			if(selected.get() == null || fields.stream().noneMatch(field -> field.getId().equals(selected.get().getId())))
				return;

			fieldsComboBox.setValue(selected.get());

		}).run();

		fieldComponent.setOnEntryRemove(field ->
		{
			deletedFieldIds.add(field.getId());
		});

		onSelectedFieldChange = field ->
		{
			if(field == null || !multipliersCheckBox.getValue())
			{
				multipliersInputLayout.setVisible(false);
				return;
			}

			selected.set(field);

			multipliersInputLayout.setVisible(true);
			changeNumberFieldsPallet(field);
		};

		fieldsComboBox.addValueChangeListener(event ->
		{
			onSelectedFieldChange.accept(event.getValue());
		});

		multipliersCheckBox.addValueChangeListener(event ->
		{
			fieldsComboBox.setVisible(event.getValue());
			onSelectedFieldChange.accept(fieldsComboBox.getValue());
		});


		fieldsComboBox.setValue(getOrderedFirstField());





		Button saveButton = new Button("save");
		saveButton.addClickShortcut(Key.ENTER);
		saveButton.setMinWidth("150px");
		saveButton.setMaxWidth("180px");
		saveButton.addClickListener(event ->
		{
			binder.validate();

			if(instMultiplierMap.values()
					.stream()
					.flatMap(map -> map.values().stream())
					.anyMatch(numberField -> numberField.isInvalid()))
				return;


			if(binder.isValid() && fieldComponent.isValid())
			{
				try
				{
					Institution institution = binder.getBean();
					institution.setLocalMultiplier(multipliersCheckBox.getValue());
					institutionService.update(institution);

					institutionMultiplierRepository.saveAll
					(
						instMultiplierMap.values()
								.stream()
								.flatMap(map -> map.keySet().stream())
								.filter(institutionMultiplier -> !deletedFieldIds.contains(institutionMultiplier.getField().getId()))
								.collect(Collectors.toSet())
					);

					deletedFieldIds.forEach(id ->
					{
						institutionMultiplierRepository.deleteByInstitutionIdAndFieldId(institution.getId(), id);
					});

					fieldComponent.accept(institution);
					institutionFieldsService.updateInstitutionFields(institution.getInstitutionFields().stream().toList(), fieldComponent.getInstitutionFieldsMap().keySet().stream().toList());


					institutionDataProvider.refreshAll();
					Notification.show("Data updated");
					this.close();

				}catch(DataIntegrityViolationException error)
				{
					Notification.show("Duplicate entry", 5000, Position.MIDDLE)
							.addThemeVariants(NotificationVariant.LUMO_ERROR);
				}
			}
		});

		Button cancelButton = new Button("cancel");
		cancelButton.addClickShortcut(Key.ESCAPE);
		cancelButton.setMinWidth("150px");
		cancelButton.setMaxWidth("180px");
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		cancelButton.addClickListener(event ->
		{
			binder.removeBean();
			this.close();
		});

		Span spacer = new Span();
		spacer.setWidthFull();

		this.add(inputLayout, fieldComponent, multipliersControlLayout, multipliersInputLayout);
		this.getFooter().add(new HorizontalLayout(spacer, saveButton, cancelButton));
		this.setCloseOnEsc(false);
		this.setCloseOnOutsideClick(false);
		this.setModal(true);
		this.open();
	}

	public void changeNumberFieldsPallet(Field newPallet)
	{
		multipliersInputLayout.removeAll();
		instMultiplierMap.get(newPallet).values().forEach(multipliersInputLayout::add);
	}

	public Set<Field> getGridFields()
	{
		return fieldComponent.getInstitutionFieldsMap().keySet()
				.stream()
				.map(InstitutionField::getField)
				.collect(Collectors.toSet());
	}

	public Field getOrderedFirstField()
	{
		return fieldsComboBox.getListDataView().getItems()
				.sorted((field1, field2) ->
				{
					return field1.getName().compareTo(field2.getName());

				}).findFirst()
				.orElse(null);
	}

	public void initInstitutionMultiplier(Field field)
	{
		instMultiplierMap.computeIfAbsent(field, t ->
		{

			Set<InstitutionMultiplier> instMultiplierByField = (item.getId() == null) ? new HashSet<InstitutionMultiplier>() :
					institutionMultiplierRepository
							.findAllByInstitutionId(item.getId())
							.stream()
							.filter(institutionMultiplier -> institutionMultiplier.getField().getId().equals(field.getId()))
							.collect(Collectors.toSet());


			return Stream.concat
					(
							instMultiplierByField
									.stream()
									.map(institutionMultiplier ->
									{
										NumberField textField = new NumberField(institutionMultiplier.getCourse().getName());
										//textField.setAllowedCharPattern("[0-9.,]");
										textField.setMin(0);
										textField.setMax(Double.MAX_VALUE);
										textField.setTooltipText(institutionMultiplier.getCourse().getName());
										textField.getElement().getStyle().set("margin", "5px");
										textField.setWidth("calc(20% - 10px)");

										textField.setPlaceholder(String.valueOf
												(
														Multiplier.getGlobalMultiplier(fieldMultiplierRepository, institutionMultiplier.getField().getId(), institutionMultiplier.getCourse().getId())

												).replace(".", ","));

										if(institutionMultiplier.isLocal())
											textField.setValue(institutionMultiplier.getMultiplier());

										textField.addValueChangeListener(e ->
										{
											institutionMultiplier.setMultiplier((textField.isEmpty())
													? Multiplier.getGlobalMultiplier(fieldMultiplierRepository, institutionMultiplier.getField().getId(), institutionMultiplier.getCourse().getId())
													: textField.getValue());

											institutionMultiplier.setLocal(!textField.isEmpty());
										});

										return new AbstractMap.SimpleEntry<>(institutionMultiplier, textField);
									}),
							courseRepository.findAll()
									.stream()
									.filter(course ->
									{
										return instMultiplierByField.stream()
												.noneMatch(institutionMultiplier ->
												{
													return institutionMultiplier.getCourse().getId().equals(course.getId());
												});
									})
									.map(course ->
									{
										InstitutionMultiplierKey id = new InstitutionMultiplierKey();
										id.setInstitutionId(item.getId());
										id.setFieldId(field.getId());
										id.setCourseId(course.getId());

										InstitutionMultiplier institutionMultiplier = new InstitutionMultiplier();
										institutionMultiplier.setId(id);
										institutionMultiplier.setInstitution(item);
										institutionMultiplier.setField(field);
										institutionMultiplier.setCourse(course);

										institutionMultiplier.setMultiplier
												(
														Multiplier.getGlobalMultiplier(fieldMultiplierRepository, field.getId(), course.getId())
												);

										NumberField textField = new NumberField(course.getName());
										//textField.setAllowedCharPattern("[0-9.,]");
										textField.setMin(0);
										textField.setMax(Double.MAX_VALUE);
										textField.setTooltipText(course.getName());
										textField.getElement().getStyle().set("margin", "5px");
										textField.setWidth("calc(20% - 10px)");

										textField.setPlaceholder(String.valueOf
												(
														Multiplier.getGlobalMultiplier(fieldMultiplierRepository, institutionMultiplier.getField().getId(), institutionMultiplier.getCourse().getId())

												).replace(".", ","));

										textField.addValueChangeListener(e ->
										{
											institutionMultiplier.setMultiplier((textField.isEmpty())
													? Multiplier.getGlobalMultiplier(fieldMultiplierRepository, institutionMultiplier.getField().getId(), institutionMultiplier.getCourse().getId())
													: textField.getValue());

											institutionMultiplier.setLocal(!textField.isEmpty());
										});

										return new AbstractMap.SimpleEntry<>(institutionMultiplier, textField);
									})

					).sorted((entry1, entry2) ->
			{
				return entry1.getKey().getCourse().getName().compareTo(entry2.getKey().getCourse().getName());

			}).collect(Collectors.toMap
					(
							Map.Entry::getKey,
							Map.Entry::getValue,
							(oldValue, newValue) -> newValue,
							LinkedHashMap::new
					));

		});
	}
	
//	public Set<InstitutionField> updateInstitutionFields(Collection<InstitutionField> oldValues,
//														 Collection<InstitutionField> newValues,
//														 InstitutionFieldsRepository repository)
//	{
//		Set<InstitutionField> institutionFieldsMap = new HashSet<InstitutionField>();
//		Set<InstitutionField> tmp = new HashSet<InstitutionField>();
//
//		if(oldValues != null)
//			institutionFieldsMap.addAll(oldValues);
//
//		institutionFieldsMap.removeAll(newValues);
//		for(InstitutionField institutionFields : institutionFieldsMap)
//		{
//			repository
//					.findById(institutionFields.getId())
//					.ifPresent(item ->
//							{
//								repository.deleteAllByInstitutionId(item.getId().getInstitutionId());
//							});
//		}
//
//		for(InstitutionField institutionField : newValues)
//		{
////			System.out.println(institutionField.toString());
//			tmp.add(repository.save(institutionField));
//		}
//
//		institutionFieldsMap = tmp;
//		return institutionFieldsMap;
//	}
}
