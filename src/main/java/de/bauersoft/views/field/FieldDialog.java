package de.bauersoft.views.field;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import de.bauersoft.data.entities.Field;
import de.bauersoft.data.providers.FieldDataProvider;
import de.bauersoft.services.FieldService;
import de.bauersoft.views.DialogState;

public class FieldDialog extends Dialog {

	public FieldDialog(FieldService service, FieldDataProvider dataProvider, Field item, DialogState state) {
		Binder<Field> binder = new Binder<Field>(Field.class);
		this.setHeaderTitle(state.toString());
		FormLayout inputLayout = new FormLayout();
		inputLayout.setResponsiveSteps(new ResponsiveStep("0", 1));
		TextField nameTextField = new TextField();
		nameTextField.setMaxLength(50);
		nameTextField.setRequired(true);
		nameTextField.setMinWidth("20em");
		inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "name"), 1);
		binder.bind(nameTextField, "name");
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
		cancelButton.setMaxWidth("200px");
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		cancelButton.addClickListener(e -> {
			binder.removeBean();
			this.close();
		});
		inputLayout.setWidth("50vw");
		inputLayout.setMaxWidth("50em");
		inputLayout.setHeight("50vh");
		inputLayout.setMaxHeight("18em");
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
