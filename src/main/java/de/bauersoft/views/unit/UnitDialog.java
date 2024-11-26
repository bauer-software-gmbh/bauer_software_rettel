package de.bauersoft.views.unit;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;

import de.bauersoft.data.entities.Unit;
import de.bauersoft.data.providers.UnitDataProvider;
import de.bauersoft.services.UnitService;
import de.bauersoft.views.DialogState;

public class UnitDialog extends Dialog {
	public UnitDialog(UnitService service,UnitDataProvider dataProvider, Unit item, DialogState state) {
		Binder<Unit> binder = new Binder<>(Unit.class);
		this.setHeaderTitle(state.toString());
		FormLayout inputLayout = new FormLayout();
		inputLayout.setResponsiveSteps(new ResponsiveStep("0", 1));
		TextField nameTextField = new TextField();
		nameTextField.setMaxLength(64);
		nameTextField.setRequired(true);
		nameTextField.setMinWidth("20em");
		TextField shorthandTextField = new TextField();
		shorthandTextField.setMaxLength(8);
		shorthandTextField.setRequired(true);
		shorthandTextField.setMinWidth("20em");
		ComboBox<Unit> parentComboBox = new ComboBox<Unit>();
		DataProvider<Unit, Void> internalDataProvider = DataProvider
				.fromFilteringCallbacks(query -> dataProvider.fetch(query), query -> dataProvider.size(query));
		parentComboBox.setItems(internalDataProvider.withConvertedFilter(value -> null));
		parentComboBox.setItemLabelGenerator(unit -> unit.getName());
		NumberField parentFactorNumberField = new NumberField();
		parentFactorNumberField.setMin(0);
		parentFactorNumberField.setMax(Float.MAX_VALUE);
		inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "name"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(shorthandTextField, "shorthand"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(parentComboBox, "parent"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(parentFactorNumberField, "factor"), 1);
		binder.bind(nameTextField, "name");
		binder.bind(shorthandTextField, "shorthand");
		binder.forField(parentComboBox).bind(Unit::getParent, Unit::setParent);
		binder.forField(parentFactorNumberField)
				.withConverter(input -> input.floatValue(), output -> output.doubleValue())
				.bind(Unit::getParent_factor, Unit::setParent_factor);
		binder.setBean(item);
		Button saveButton = new Button("save");
		saveButton.setMinWidth("150px");
		saveButton.setMaxWidth("180px");
		saveButton.addClickListener(e -> {
			if (binder.isValid()) {
				service.update(binder.getBean());
				dataProvider.refreshAll();
				Notification.show("Data updated");
				this.close();
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
		inputLayout.setMaxHeight("13em");
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
