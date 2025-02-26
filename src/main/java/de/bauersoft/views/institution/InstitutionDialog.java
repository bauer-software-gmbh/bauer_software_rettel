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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.dom.Style;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.providers.AddressDataProvider;
import de.bauersoft.data.providers.InstitutionDataProvider;
import de.bauersoft.services.*;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.address.AddressComboBox;
import de.bauersoft.views.institution.institutionFields.FieldDragComponent;
import lombok.Getter;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

@Getter
public class InstitutionDialog extends Dialog
{
	private InstitutionService institutionService;
	private InstitutionFieldsService institutionFieldsService;
	private AddressService addressService;
	private FieldService fieldService;
	private UserService userService;
	private InstitutionMultiplierService institutionMultiplierService;
	private CourseService courseService;
	private FieldMultiplierService fieldMultiplierService;
	private AllergenService allergenService;
	private InstitutionAllergenService institutionAllergenService;
	private PatternService patternService;
	private InstitutionPatternService institutionPatternService;
	private InstitutionClosingTimeService institutionClosingTimeService;

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
	private FieldDragComponent fieldDragComponent;

	public InstitutionDialog(InstitutionService institutionService, InstitutionFieldsService institutionFieldsService, AddressService addressService, FieldService fieldService, UserService userService, InstitutionMultiplierService institutionMultiplierService, CourseService courseService, FieldMultiplierService fieldMultiplierService, AllergenService allergenService, InstitutionAllergenService institutionAllergenService, PatternService patternService, InstitutionPatternService institutionPatternService, InstitutionClosingTimeService institutionClosingTimeService, InstitutionDataProvider institutionDataProvider, AddressDataProvider addressDataProvider, Institution item, DialogState dialogState)
	{
		this.institutionService = institutionService;
		this.institutionFieldsService = institutionFieldsService;
		this.addressService = addressService;
		this.fieldService = fieldService;
		this.userService = userService;
        this.institutionMultiplierService = institutionMultiplierService;
        this.courseService = courseService;
        this.fieldMultiplierService = fieldMultiplierService;
        this.allergenService = allergenService;
        this.institutionAllergenService = institutionAllergenService;
        this.patternService = patternService;
        this.institutionPatternService = institutionPatternService;
        this.institutionClosingTimeService = institutionClosingTimeService;
        this.institutionDataProvider = institutionDataProvider;
        this.addressDataProvider = addressDataProvider;
        this.item = item;
		this.dialogState = dialogState;

		this.setHeaderTitle(dialogState.toString());

		Binder<Institution> binder = new Binder<>(Institution.class);

		inputLayout = new FormLayout();
		inputLayout.setWidthFull();
		inputLayout.setResponsiveSteps(new ResponsiveStep("0", 1));

		nameTextField = new TextField();
		nameTextField.setMaxLength(64);
		nameTextField.setRequired(true);
		nameTextField.setWidthFull();

		descriptionTextArea = new TextArea();
		descriptionTextArea.setMaxLength(1024);
		descriptionTextArea.setSizeFull();
		descriptionTextArea.setHeight("calc(3* var(--lumo-text-field-size))");
		descriptionTextArea.setMaxHeight("calc(3* var(--lumo-text-field-size))");

		customerIdTextField = new TextField();
		customerIdTextField.setWidthFull();
		customerIdTextField.setMaxLength(256);

		datePickerLayout = new HorizontalLayout();
		datePickerLayout.setWidthFull();

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

		fieldDragComponent = new FieldDragComponent(this);
		fieldDragComponent.setFieldPool(fieldService.getRepository().findAll());
		fieldDragComponent.updateView();

		inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "Name"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(descriptionTextArea, "Beschreibung"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(customerIdTextField, "Kunden Nr. "), 1);
		inputLayout.setColspan(inputLayout.addFormItem(datePickerLayout, "Bestellung"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(addressComboBox, "Adresse"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(userMultiSelectComboBox, "Benutzer"), 1);



		binder.forField(nameTextField).asRequired((value, context) ->
		{
			return (value != null && !value.isBlank())
					? ValidationResult.ok()
					: ValidationResult.error("Name ist erforderlich");

		}).bind(Institution::getName, Institution::setName);

		binder.bind(descriptionTextArea, Institution::getDescription, Institution::setDescription);

		binder.bind(customerIdTextField, institution ->
		{
			return Objects.requireNonNullElse(institution.getCustomerId(), "").trim();

		}, (institution, s) ->
		{
			institution.setCustomerId(Objects.requireNonNullElse(s, "").trim());
		});

		binder.forField(orderStartTimePicker).withValidator((value, context) ->
		{
			if(value == null || value.isAfter(orderEndTimePicker.getValue()))
				return ValidationResult.error("Startzeit muss vor Endzeit liegen!");

			if(value == null || value.isBefore(LocalTime.of(0, 5)))
				return ValidationResult.error("Startzeit darf frühestens um 00:05 Uhr beginnen!");

			return ValidationResult.ok();

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
			fieldDragComponent.loadTemporaries();
			this.close();
		});

		this.setWidth("50rem");
		this.setMaxWidth("65vw");
		this.setHeight("62.5rem");
		this.setMaxHeight("100vh");

		this.add(inputLayout, fieldDragComponent);
		this.getFooter().add(saveButton, cancelButton);
		this.setCloseOnEsc(false);
		this.setCloseOnOutsideClick(false);
		this.setModal(true);
		this.open();
	}
}
