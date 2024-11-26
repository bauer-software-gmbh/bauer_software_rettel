package de.bauersoft.views.institution;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import de.bauersoft.data.entities.Institution;
import de.bauersoft.data.entities.InstitutionFields;
import de.bauersoft.data.entities.User;
import de.bauersoft.data.providers.InstitutionDataProvider;
import de.bauersoft.data.repositories.address.AddressRepository;
import de.bauersoft.data.repositories.field.FieldRepository;
import de.bauersoft.data.repositories.field.InstitutionFieldsRepository;
import de.bauersoft.data.repositories.user.UserRepository;
import de.bauersoft.services.AddressService;
import de.bauersoft.services.InstitutionService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.address.AddressComboBox;

public class InstitutionDialog extends Dialog {

	public InstitutionDialog(InstitutionService service, AddressService addressService, FieldRepository fieldRepository,InstitutionFieldsRepository institutionFieldsRepository,  
			UserRepository userRepository, AddressRepository addressRepository, InstitutionDataProvider dataProvider, Institution item,
			DialogState state) {
		this.setHeaderTitle(state.toString());
		Binder<Institution> binder = new Binder<>(Institution.class);
		FormLayout inputLayout = new FormLayout();
		inputLayout.setResponsiveSteps(new ResponsiveStep("0", 1));
		TextField nameTextField = new TextField();
		nameTextField.setMaxLength(64);
		nameTextField.setRequired(true);
		nameTextField.setMinWidth("20em");
		TextArea descriptionTextArea = new TextArea();
		descriptionTextArea.setMaxLength(512);
		descriptionTextArea.setSizeFull();
		descriptionTextArea.setMinHeight("calc(4* var(--lumo-text-field-size))");
		AddressComboBox addressComboBox = new AddressComboBox(addressRepository,addressService);
		addressComboBox.setRequired(true);
		addressComboBox.setItemLabelGenerator(address -> address.getStreet() + " " + address.getHouseNumber() + " "
				+ address.getPostalCode() + " " + address.getCity());
		addressComboBox.setItems(addressRepository.findAll());
		addressComboBox.setWidthFull();

		MultiSelectComboBox<User> userMultiSelectComboBox = new MultiSelectComboBox<User>();
		userMultiSelectComboBox
				.setItemLabelGenerator(user -> user.getName() + " " + user.getSurname() + " [" + user.getEmail() + "]");
		userMultiSelectComboBox.setItems(userRepository.findAll());
		userMultiSelectComboBox.setWidthFull();
		FieldComponent fieldComponent = new FieldComponent(item);
		fieldComponent.setItems(fieldRepository.findAll());
		fieldComponent.setValue(item.getFields());
		fieldComponent.setHeight("50vh");
		inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "name"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(descriptionTextArea, "description"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(addressComboBox, "address"), 1);
		// inputLayout.setColspan(inputLayout.addFormItem(fieldComponent, "field"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(userMultiSelectComboBox, "user"), 1);
		binder.forField(nameTextField).asRequired().bind("name");
		binder.bind(descriptionTextArea, "description");
		binder.bind(addressComboBox, "address");
		// binder.bind(fieldComponent, "fields");
		binder.bind(userMultiSelectComboBox, "users");
		binder.setBean(item);
		Button saveButton = new Button("save");
		saveButton.setMinWidth("150px");
		saveButton.setMaxWidth("180px");
		saveButton.addClickListener(event -> {
			binder.validate();
			if (binder.isValid()) {
				try {
					
					Set<InstitutionFields> oldInstitutionFields = binder.getBean().getFields();
					fieldComponent.accept(binder.getBean());
					updateInstitutionFields(oldInstitutionFields,fieldComponent.getInstitutionFields(),institutionFieldsRepository) ;
					service.update(item);
					dataProvider.refreshAll();
					Notification.show("Data updated");
					this.close();
				} catch (DataIntegrityViolationException error) {
					Notification.show("Duplicate entry", 5000, Position.MIDDLE)
							.addThemeVariants(NotificationVariant.LUMO_ERROR);
				}
			}
		});
		Button cancelButton = new Button("cancel");
		cancelButton.setMinWidth("150px");
		cancelButton.setMaxWidth("180px");
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		cancelButton.addClickListener(event -> {
			binder.removeBean();
			this.close();
		});
		inputLayout.setWidth("50vw");
		inputLayout.setMaxWidth("50em");
		inputLayout.setHeight("50vh");
		inputLayout.setMaxHeight("20em");
		Span spacer = new Span();
		spacer.setWidthFull();
		this.add(inputLayout,fieldComponent);
		this.getFooter().add(new HorizontalLayout(spacer, saveButton, cancelButton));
		this.setCloseOnEsc(false);
		this.setCloseOnOutsideClick(false);
		this.setModal(true);
		this.open();
	}
	
	public Set<InstitutionFields> updateInstitutionFields(Collection<InstitutionFields> oldValues, Collection<InstitutionFields> newValues,
			InstitutionFieldsRepository repository) {
		Set<InstitutionFields> institutionFieldsMap = new HashSet<InstitutionFields>();
		Set<InstitutionFields> tmp = new HashSet<InstitutionFields>();
		if (oldValues != null) {
			institutionFieldsMap.addAll(oldValues);
		}
		institutionFieldsMap.removeAll(newValues);
		for(InstitutionFields institutionFields :institutionFieldsMap) {
			repository.findById(institutionFields.getId()).ifPresent(item->
			repository.deleteAllByInstitutionId(item.getId().getInstitutionId())
			);
		}
		for (InstitutionFields formulation : newValues) {
			tmp.add(repository.save(formulation));
		}	
		institutionFieldsMap = tmp;
		return institutionFieldsMap;
	}
}
