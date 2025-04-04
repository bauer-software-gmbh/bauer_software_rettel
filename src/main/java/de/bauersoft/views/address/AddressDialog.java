package de.bauersoft.views.address;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.address.Address;
import de.bauersoft.data.providers.AddressDataProvider;
import de.bauersoft.services.AddressService;
import de.bauersoft.views.DialogState;
import org.springframework.dao.DataIntegrityViolationException;

public class AddressDialog extends Dialog
{
    private final FilterDataProvider<Address, Long> filterDataProvider;
    private final AddressService addressService;
    private Address item;
    private DialogState state;

    public AddressDialog(FilterDataProvider<Address, Long> filterDataProvider, AddressService addressService, Address item, DialogState state)
    {
        this.filterDataProvider = filterDataProvider;
        this.addressService = addressService;
        this.item = item;
        this.state = state;

		this.setHeaderTitle(state.toString());

        Binder<Address> binder = new Binder<Address>(Address.class);

        FormLayout inputLayout = new FormLayout();
		inputLayout.setWidth("30rem");

        inputLayout.setResponsiveSteps(new ResponsiveStep("0", 1));

        TextField streetTextField = new TextField();
        streetTextField.setMaxLength(64);
        streetTextField.setAutofocus(true);
        streetTextField.setRequired(true);
        streetTextField.setWidthFull();

        TextField houseNumberTextField = new TextField();
        houseNumberTextField.setMaxLength(8);
        houseNumberTextField.setRequired(true);
        houseNumberTextField.setWidthFull();

        TextField postalCodeTextField = new TextField();
        postalCodeTextField.setMaxLength(5);
        postalCodeTextField.setRequired(true);
        postalCodeTextField.setWidthFull();

        TextField cityTextField = new TextField();
        cityTextField.setMaxLength(64);
        cityTextField.setRequired(true);
        cityTextField.setWidthFull();

        inputLayout.setColspan(inputLayout.addFormItem(streetTextField, "Straße"), 1);
        inputLayout.setColspan(inputLayout.addFormItem(houseNumberTextField, "Hausnummer"), 1);
        inputLayout.setColspan(inputLayout.addFormItem(postalCodeTextField, "PLZ"), 1);
        inputLayout.setColspan(inputLayout.addFormItem(cityTextField, "Ort"), 1);

        binder.forField(streetTextField).asRequired((value, context) ->
		{
			return (value != null && !value.isBlank())
					? ValidationResult.ok()
					: ValidationResult.error("Straße ist erforderlich");
		}).bind(Address::getStreet, Address::setStreet);

        binder.forField(houseNumberTextField).asRequired((value, context) ->
		{
			return (value != null && !value.isBlank())
					? ValidationResult.ok()
					: ValidationResult.error("Hausnummer ist erforderlich");
		}).bind(Address::getNumber, Address::setNumber);

        binder.forField(postalCodeTextField).asRequired((value, context) ->
		{
			return (value != null && !value.isBlank())
					? ValidationResult.ok()
					: ValidationResult.error("PLZ ist erforderlich");
		}).bind(Address::getPostal, Address::setPostal);

        binder.forField(cityTextField).asRequired((value, context) ->
		{
			return (value != null && !value.isBlank())
					? ValidationResult.ok()
					: ValidationResult.error("Ort ist erforderlich");
		}).bind(Address::getCity, Address::setCity);

        binder.readBean(item);

        Button saveButton = new Button("Speichern");
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.setMinWidth("150px");
        saveButton.setMaxWidth("180px");
        saveButton.addClickListener(event ->
        {
			binder.writeBeanIfValid(item);
            if(binder.isValid())
            {
                try
                {
                    addressService.update(item);
					filterDataProvider.refreshAll();

                    Notification.show("Daten wurden aktualisiert");
                    this.close();

                }catch(DataIntegrityViolationException error)
                {
                    Notification.show("Doppelter Eintrag", 5000, Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
        });

        Button cancelButton = new Button("Abbrechen");
        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.setMinWidth("150px");
        cancelButton.setMaxWidth("180px");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(e ->
        {
            binder.removeBean();
            filterDataProvider.refreshAll();
            this.close();
        });

        this.add(inputLayout);
        this.getFooter().add(saveButton, cancelButton);
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);
        this.setModal(true);
        this.open();
    }
}
