package de.bauersoft.views.address;

import org.springframework.dao.DataIntegrityViolationException;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import de.bauersoft.data.entities.Address;
import de.bauersoft.data.providers.AddressDataProvider;
import de.bauersoft.services.AddressService;
import de.bauersoft.views.DialogState;

public class AddressDialog extends Dialog {
	
	public AddressDialog(AddressService service,AddressDataProvider dataProvider, Address item, DialogState state) {
		Binder<Address> binder = new Binder<Address>(Address.class);
		this.setHeaderTitle(state.toString());
		FormLayout inputLayout = new FormLayout();
		inputLayout.setResponsiveSteps(new ResponsiveStep("0", 1));
		
		TextField streetTextField = new TextField();
		streetTextField.setMaxLength(64);
		streetTextField.setRequired(true);
		streetTextField.setMinWidth("20em");
	
		TextField houseNumberTextField = new TextField();
		houseNumberTextField.setMaxLength(64);
		houseNumberTextField.setRequired(true);
		houseNumberTextField.setMinWidth("20em");
	
		TextField postalCodeTextField = new TextField();
		postalCodeTextField.setMaxLength(64);
		postalCodeTextField.setRequired(true);
		postalCodeTextField.setMinWidth("20em");
	
		TextField cityTextField = new TextField();
		cityTextField.setMaxLength(64);
		cityTextField.setRequired(true);
		cityTextField.setMinWidth("20em");
	
		inputLayout.setColspan(inputLayout.addFormItem(streetTextField, "street"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(houseNumberTextField, "house Number"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(postalCodeTextField, "postal code"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(cityTextField, "city"), 1);
		
		binder.bind(streetTextField, "street");
		binder.bind(houseNumberTextField, "houseNumber");
		binder.bind(postalCodeTextField, "postalCode");
		binder.bind(cityTextField, "city");
		binder.setBean(item);
		Button saveButton = new Button("save");
		saveButton.setMinWidth("150px");
		saveButton.setMaxWidth("180px");
		saveButton.addClickListener(event -> {
			if (binder.isValid()) {
				try {
					service.update(binder.getBean());
					if(dataProvider !=null) dataProvider.refreshAll();
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
		cancelButton.addClickListener(e -> {
			binder.removeBean();
			this.close();
		});
		inputLayout.setWidth("50vw");
		inputLayout.setMaxWidth("50em");
		inputLayout.setHeight("50vh");
		inputLayout.setMaxHeight("20em");
		Span spacer = new Span();
		spacer.setWidthFull();
		this.add(inputLayout);
		this.getFooter().add(new HorizontalLayout(spacer, saveButton, cancelButton));
		this.setCloseOnEsc(false);
		this.setCloseOnOutsideClick(false);
		this.setModal(true);
		this.open();
	}
}
