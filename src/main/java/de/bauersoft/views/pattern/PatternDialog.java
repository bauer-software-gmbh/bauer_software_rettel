package de.bauersoft.views.pattern;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.providers.PatternDataProvider;
import de.bauersoft.services.PatternService;
import de.bauersoft.views.DialogState;
import org.springframework.dao.DataIntegrityViolationException;

public class PatternDialog extends Dialog
{

    public PatternDialog(PatternService service, PatternDataProvider dataProvider, Pattern item, DialogState state)
    {
        this.setHeaderTitle(state.toString());

        Binder<Pattern> binder = new Binder<Pattern>(Pattern.class);

        FormLayout inputLayout = new FormLayout();
		inputLayout.setWidth("50vw");
		inputLayout.setMaxWidth("50em");
		inputLayout.setHeight("50vh");
		inputLayout.setMaxHeight("18em");

        inputLayout.setResponsiveSteps(new ResponsiveStep("0", 1));

        TextField nameTextField = new TextField();
        nameTextField.setMaxLength(50);
        nameTextField.setRequired(true);
        nameTextField.setMinWidth("20em");

        TextArea descriptionTextArea = new TextArea();
        descriptionTextArea.setMaxLength(512);
        descriptionTextArea.setSizeFull();
        descriptionTextArea.setMinHeight("calc(4* var(--lumo-text-field-size))");

        Checkbox religiousCheckbox = new Checkbox();

        inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "name"), 1);
        inputLayout.setColspan(inputLayout.addFormItem(descriptionTextArea, "description"), 1);
        inputLayout.setColspan(inputLayout.addFormItem(religiousCheckbox, "religious"), 1);

        binder.forField(nameTextField).asRequired((value, context) ->
        {
            return (value != null && !value.isBlank())
                    ? ValidationResult.ok()
                    : ValidationResult.error("Name is required");

        }).bind(Pattern::getName, Pattern::setName);
        binder.bind(descriptionTextArea, "description");
		binder.forField(religiousCheckbox).bind(Pattern::isReligious, Pattern::setReligious);

        binder.setBean(item);

        Button saveButton = new Button("save");
		saveButton.addClickShortcut(Key.ENTER);
        saveButton.setMinWidth("150px");
        saveButton.setMaxWidth("180px");
        saveButton.addClickListener(e ->
        {
            binder.validate();
            if(binder.isValid())
            {
				try
				{
					service.update(binder.getBean());
					dataProvider.refreshAll();
					Notification.show("Data updated");
					this.close();

				}catch(DataIntegrityViolationException error)
				{
					Notification.show("Duplicate entry", 5000, Notification.Position.MIDDLE)
							.addThemeVariants(NotificationVariant.LUMO_ERROR);
				}
            }
        });

        Button cancelButton = new Button("cancel");
		cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.setMinWidth("150px");
        cancelButton.setMaxWidth("200px");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(e ->
        {
            binder.removeBean();
            this.close();
        });

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
