package de.bauersoft.views.pattern;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.services.PatternService;
import de.bauersoft.views.DialogState;
import org.springframework.dao.DataIntegrityViolationException;

public class PatternDialog extends Dialog
{
    private final FilterDataProvider<Pattern, Long> filterDataProvider;
    private final PatternService patternService;
    private final Pattern item;
    private final DialogState state;

    public PatternDialog(FilterDataProvider<Pattern, Long> filterDataProvider, PatternService patternService, Pattern item, DialogState state)
    {
        this.filterDataProvider = filterDataProvider;
        this.patternService = patternService;
        this.item = item;
        this.state = state;

        this.setHeaderTitle(state.toString());

        Binder<Pattern> binder = new Binder<>(Pattern.class);

        FormLayout inputLayout = new FormLayout();
		inputLayout.setWidth("30rem");

        inputLayout.setResponsiveSteps(new ResponsiveStep("0", 1));

        TextField nameTextField = new TextField();
        nameTextField.setWidthFull();
        nameTextField.setMaxLength(64);
        nameTextField.setAutofocus(true);
        nameTextField.setRequired(true);
        nameTextField.setEnabled(state == DialogState.NEW);

        TextArea descriptionTextArea = new TextArea();
        descriptionTextArea.setMaxLength(1024);
        descriptionTextArea.setSizeFull();
        descriptionTextArea.setMinHeight("calc(4* var(--lumo-text-field-size))");

        Checkbox religiousCheckbox = new Checkbox();

        inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "Name"), 1);
        inputLayout.setColspan(inputLayout.addFormItem(descriptionTextArea, "Beschreibung"), 1);
        inputLayout.setColspan(inputLayout.addFormItem(religiousCheckbox, "Religiös"), 1);

        binder.forField(nameTextField).asRequired((value, context) ->
        {
            return (value != null && !value.isBlank())
                    ? ValidationResult.ok()
                    : ValidationResult.error("Name ist erforderlich");

        }).bind(Pattern::getName, Pattern::setName);
        binder.bind(descriptionTextArea, "description");
		binder.forField(religiousCheckbox).bind(Pattern::isReligious, Pattern::setReligious);

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
					patternService.update(item);
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
        cancelButton.setMaxWidth("200px");
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
