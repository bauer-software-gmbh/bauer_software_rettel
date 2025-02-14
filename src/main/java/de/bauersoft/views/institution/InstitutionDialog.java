package de.bauersoft.views.institution;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.dom.Style;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institution.InstitutionMultiplier;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.providers.AddressDataProvider;
import de.bauersoft.data.providers.InstitutionDataProvider;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.field.FieldMultiplierRepository;
import de.bauersoft.data.repositories.institutionMultiplier.InstitutionMultiplierRepository;
import de.bauersoft.services.*;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.address.AddressComboBox;
import de.bauersoft.views.institution.institutionFields.FieldDragComponent;
import lombok.Getter;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Getter
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
	private AllergenService allergenService;
	private InstitutionAllergenService institutionAllergenService;
	private PatternService patternService;
	private InstitutionPatternService institutionPatternService;

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
	//private FieldComponent fieldComponent;

	private final LinkedHashMap<Field, LinkedHashMap<InstitutionMultiplier, NumberField>> instMultiplierMap;
	private final Set<Long> deletedFieldIds;

	private final AtomicReference<Field> selected;

//	private final Consumer<Field> onSelectedFieldChange;
//	private final HorizontalLayout multipliersControlLayout;
//	private final FlexLayout multipliersInputLayout;
//	private final Checkbox multipliersCheckBox;
//	private final ComboBox<Field> fieldsComboBox;

	public InstitutionDialog(InstitutionService institutionService, InstitutionFieldsService institutionFieldsService, AddressService addressService, FieldService fieldService, UserService userService, InstitutionMultiplierService institutionMultiplierService, CourseService courseService, FieldMultiplierService fieldMultiplierService, AllergenService allergenService, InstitutionAllergenService institutionAllergenService, PatternService patternService, InstitutionPatternService institutionPatternService, InstitutionDataProvider institutionDataProvider, AddressDataProvider addressDataProvider, Institution item, DialogState dialogState)
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
        this.allergenService = allergenService;
        this.institutionAllergenService = institutionAllergenService;
        this.patternService = patternService;
        this.institutionPatternService = institutionPatternService;
        this.institutionDataProvider = institutionDataProvider;
        this.addressDataProvider = addressDataProvider;
        this.item = item;
		this.dialogState = dialogState;

		instMultiplierMap = new LinkedHashMap<>();
		deletedFieldIds = new HashSet<>();

		selected = new AtomicReference<>();

		this.setHeaderTitle(dialogState.toString());

		Binder<Institution> binder = new Binder<>(Institution.class);

		inputLayout = new FormLayout();
		inputLayout.setWidth("50vw");
		inputLayout.setMaxWidth("50em");
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
		customerIdTextField.setMaxLength(256);

		datePickerLayout = new HorizontalLayout();

		Span orderStartSpan = new Span("Von ");
		orderStartSpan.getStyle().setAlignSelf(Style.AlignSelf.CENTER);

		orderStartTimePicker = new TimePicker();

		Span orderEndSpan = new Span(" bis ");
		orderEndSpan.getStyle().setAlignSelf(Style.AlignSelf.CENTER);

		orderEndTimePicker = new TimePicker();

		datePickerLayout.add(orderStartSpan, orderStartTimePicker, orderEndSpan, orderEndTimePicker);

		addressComboBox = new AddressComboBox(addressService, addressDataProvider);
		addressComboBox.setRequired(true);
		addressComboBox.setItemLabelGenerator(address ->
				{
					return address.getStreet() + " " + address.getNumber() + " " + address.getPostal() + " " + address.getCity();
				});
		addressComboBox.setItems(addressService.getRepository().findAll());
		addressComboBox.setWidthFull();

		userMultiSelectComboBox = new MultiSelectComboBox<>();
		userMultiSelectComboBox.setItemLabelGenerator(user ->
				{
					return user.getName() + " " + user.getSurname() + " [" + user.getEmail() + "]";
				});
		userMultiSelectComboBox.setItems(userService.getRepository().findAll());
		userMultiSelectComboBox.setWidthFull();

		FieldDragComponent fieldDragComponent = new FieldDragComponent(this);
		fieldDragComponent.setFieldPool(fieldService.getRepository().findAll());
		fieldDragComponent.updateView();

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
					: ValidationResult.error("Name ist erforderlich");

		}).bind(Institution::getName, Institution::setName);

		binder.bind(descriptionTextArea, Institution::getDescription, Institution::setDescription);
		binder.bind(customerIdTextField, Institution::getCustomerId, Institution::setCustomerId);

		binder.forField(orderStartTimePicker).withValidator((value, context) ->
		{
			return (value != null && value.isBefore(orderEndTimePicker.getValue())) ?
					ValidationResult.ok() :
					ValidationResult.error("Startzeit muss vor Endzeit liegen!");

		}).bind(Institution::getOrderStart, Institution::setOrderStart);

		binder.forField(orderEndTimePicker).withValidator((value, context) ->
		{
			return (value != null && value.isAfter(orderStartTimePicker.getValue())) ?
					ValidationResult.ok() :
					ValidationResult.error("Endzeit muss nach Startzeit liegen!");

		}).bind(Institution::getOrderEnd, Institution::setOrderEnd);

		binder.bind(addressComboBox, Institution::getAddress, Institution::setAddress);
		binder.bind(userMultiSelectComboBox, Institution::getUsers, Institution::setUsers);

		binder.readBean(item);

		Button saveButton = new Button("Speichern");
		saveButton.addClickShortcut(Key.ENTER);
		saveButton.setMinWidth("150px");
		saveButton.setMaxWidth("180px");
		saveButton.addClickListener(event ->
		{
			try
			{
				binder.writeBean(item);

				institutionService.update(item);

				fieldDragComponent.updateInstitutionFields(item.getInstitutionFields().stream().toList());

				institutionDataProvider.refreshAll();

				Notification.show("Daten wurden aktualisiert");
				this.close();

			}catch(DataIntegrityViolationException error)
			{
				Notification.show("Doppelter Eintrag", 5000, Position.MIDDLE)
						.addThemeVariants(NotificationVariant.LUMO_ERROR);

			}catch(ValidationException err)
			{
			}
		});

		Button cancelButton = new Button("Abbrechen");
		cancelButton.addClickShortcut(Key.ESCAPE);
		cancelButton.setMinWidth("150px");
		cancelButton.setMaxWidth("180px");
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		cancelButton.addClickListener(event ->
		{
			this.close();
		});

		this.add(inputLayout, fieldDragComponent);
		this.getFooter().add(saveButton, cancelButton);
		this.setCloseOnEsc(false);
		this.setCloseOnOutsideClick(false);
		this.setModal(true);
		this.open();
	}

//	public void changeNumberFieldsPallet(Field newPallet)
//	{
//		multipliersInputLayout.removeAll();
//		instMultiplierMap.get(newPallet).values().forEach(multipliersInputLayout::add);
//	}

	public Set<Field> getGridFields()
	{
		return new HashSet<>();
//		return fieldComponent.getInstitutionFieldsMap().keySet()
//				.stream()
//				.map(InstitutionField::getField)
//				.collect(Collectors.toSet());
	}

//	public Field getOrderedFirstField()
//	{
//		return fieldsComboBox.getListDataView().getItems()
//				.sorted((field1, field2) ->
//				{
//					return field1.getName().compareTo(field2.getName());
//
//				}).findFirst()
//				.orElse(null);
//	}
//
//	public void initInstitutionMultiplier(Field field)
//	{
//		instMultiplierMap.computeIfAbsent(field, t ->
//		{
//
//			Set<InstitutionMultiplier> instMultiplierByField = (item.getId() == null) ? new HashSet<InstitutionMultiplier>() :
//					institutionMultiplierRepository
//							.findAllByInstitutionId(item.getId())
//							.stream()
//							.filter(institutionMultiplier -> institutionMultiplier.getInstitutionField().getField().getId().equals(field.getId()))
//							.collect(Collectors.toSet());
//
//
//			return Stream.concat
//					(
//							instMultiplierByField
//									.stream()
//									.map(institutionMultiplier ->
//									{
//										NumberField textField = new NumberField(institutionMultiplier.getCourse().getName());
//										//textField.setAllowedCharPattern("[0-9.,]");
//										textField.setMin(0);
//										textField.setMax(Double.MAX_VALUE);
//										textField.setTooltipText(institutionMultiplier.getCourse().getName());
//										textField.getElement().getStyle().set("margin", "5px");
//										textField.setWidth("calc(20% - 10px)");
//
//										textField.setPlaceholder(String.valueOf
//												(
//														//Multiplier.getGlobalMultiplier(fieldMultiplierRepository, institutionMultiplier.getInstitutionField().getField().getId(), institutionMultiplier.getCourse().getId())
//														0
//
//												).replace(".", ","));
//
//										if(institutionMultiplier.isLocal())
//											textField.setValue(institutionMultiplier.getMultiplier());
//
//										textField.addValueChangeListener(e ->
//										{
//											institutionMultiplier.setMultiplier((textField.isEmpty())
//													? 0//Multiplier.getGlobalMultiplier(fieldMultiplierRepository, institutionMultiplier.getInstitutionField().getField().getId(), institutionMultiplier.getCourse().getId())
//													: textField.getValue());
//
//											institutionMultiplier.setLocal(!textField.isEmpty());
//										});
//
//										return new AbstractMap.SimpleEntry<>(institutionMultiplier, textField);
//									}),
//							courseRepository.findAll()
//									.stream()
//									.filter(course ->
//									{
//										return instMultiplierByField.stream()
//												.noneMatch(institutionMultiplier ->
//												{
//													return institutionMultiplier.getCourse().getId().equals(course.getId());
//												});
//									})
//									.map(course ->
//									{
//										InstitutionMultiplierKey id = new InstitutionMultiplierKey();
////										id.setInstitutionId(item.getId());
////										id.setFieldId(field.getId());
//										id.setCourseId(course.getId());
//
//										InstitutionMultiplier institutionMultiplier = new InstitutionMultiplier();
//										institutionMultiplier.setId(id);
////										institutionMultiplier.setInstitution(item);
////										institutionMultiplier.setField(field);
//										institutionMultiplier.setCourse(course);
//
//										institutionMultiplier.setMultiplier
//												(
//														0//Multiplier.getGlobalMultiplier(fieldMultiplierRepository, field.getId(), course.getId())
//												);
//
//										NumberField textField = new NumberField(course.getName());
//										//textField.setAllowedCharPattern("[0-9.,]");
//										textField.setMin(0);
//										textField.setMax(Double.MAX_VALUE);
//										textField.setTooltipText(course.getName());
//										textField.getElement().getStyle().set("margin", "5px");
//										textField.setWidth("calc(20% - 10px)");
//
//										textField.setPlaceholder(String.valueOf
//												(
//														0//Multiplier.getGlobalMultiplier(fieldMultiplierRepository, institutionMultiplier.getInstitutionField().getField().getId(), institutionMultiplier.getCourse().getId())
//
//												).replace(".", ","));
//
//										textField.addValueChangeListener(e ->
//										{
//											institutionMultiplier.setMultiplier((textField.isEmpty())
//													?0// Multiplier.getGlobalMultiplier(fieldMultiplierRepository, institutionMultiplier.getInstitutionField().getField().getId(), institutionMultiplier.getCourse().getId())
//													: textField.getValue());
//
//											institutionMultiplier.setLocal(!textField.isEmpty());
//										});
//
//										return new AbstractMap.SimpleEntry<>(institutionMultiplier, textField);
//									})
//
//					).sorted((entry1, entry2) ->
//			{
//				return entry1.getKey().getCourse().getName().compareTo(entry2.getKey().getCourse().getName());
//
//			}).collect(Collectors.toMap
//					(
//							Map.Entry::getKey,
//							Map.Entry::getValue,
//							(oldValue, newValue) -> newValue,
//							LinkedHashMap::new
//					));
//
//		});
//	}
	
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
