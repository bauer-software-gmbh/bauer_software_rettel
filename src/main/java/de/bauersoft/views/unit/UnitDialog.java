package de.bauersoft.views.unit;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.provider.DataProvider;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.unit.Unit;
import de.bauersoft.data.providers.UnitDataProvider;
import de.bauersoft.services.UnitService;
import de.bauersoft.views.DialogState;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;

import java.util.Objects;

public class UnitDialog extends Dialog
{
	private final FilterDataProvider<Unit, Long> filterDataProvider;
	private final UnitService unitService;
	private final Unit item;

	private final DialogState state;

	public UnitDialog(FilterDataProvider<Unit, Long> filterDataProvider, UnitService unitService, Unit item, DialogState state)
	{
        this.filterDataProvider = filterDataProvider;
        this.unitService = unitService;
        this.item = item;
        this.state = state;
		this.setHeaderTitle(state.toString());

		Binder<Unit> binder = new Binder<>(Unit.class);

		FormLayout inputLayout = new FormLayout();
		inputLayout.setWidth("30rem");
		inputLayout.setResponsiveSteps(new ResponsiveStep("0", 1));

		TextField nameTextField = new TextField();
		nameTextField.setMaxLength(64);
		nameTextField.setRequired(true);
		nameTextField.setAutofocus(true);
		nameTextField.setWidthFull();

		TextField shorthandTextField = new TextField();
		shorthandTextField.setMaxLength(8);
		shorthandTextField.setRequired(true);
		shorthandTextField.setWidth("5em");

		ComboBox<Unit> parentComboBox = new ComboBox<Unit>();
		parentComboBox.setWidthFull();
		parentComboBox.setItemLabelGenerator(Unit::getName);
		parentComboBox.setItems(unitService.findAll());

		NumberField parentFactorNumberField = new NumberField();
		parentFactorNumberField.setWidthFull();
		parentFactorNumberField.setMin(0);
		parentFactorNumberField.setMax(Float.MAX_VALUE);

		inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "Name"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(shorthandTextField, "Abkürzung"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(parentComboBox, "Parent"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(parentFactorNumberField, "Faktor"), 1);

		binder.forField(nameTextField).asRequired((value, context) ->
		{
			return (value != null && !value.isBlank())
					? ValidationResult.ok()
					: ValidationResult.error("Name ist erforderlich");

		}).bind(Unit::getName, Unit::setName);

		binder.forField(shorthandTextField).asRequired((value, context) ->
		{
			return (value != null && !value.isBlank())
					? ValidationResult.ok()
					: ValidationResult.error("Abkürzung ist erforderlich");

		}).bind(Unit::getShorthand, Unit::setShorthand);

		binder.forField(parentComboBox).bind(Unit::getParentUnit, Unit::setParentUnit);
		binder.forField(parentFactorNumberField).bind(Unit::getParentFactor, Unit::setParentFactor);

		binder.readBean(item);

		Button saveButton = new Button("Speichern");
		saveButton.addClickShortcut(Key.ENTER);
		saveButton.setMinWidth("150px");
		saveButton.setMaxWidth("180px");
		saveButton.addClickListener(e ->
		{
			binder.writeBeanIfValid(item);
			if(binder.isValid())
			{
				try
				{
					unitService.update(item);
					filterDataProvider.refreshAll();

					Notification.show("Daten wurden aktualisiert");
					this.close();

				}catch(DataIntegrityViolationException error)
				{
					Notification.show("Doppelter Eintrag", 5000, Notification.Position.MIDDLE)
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
