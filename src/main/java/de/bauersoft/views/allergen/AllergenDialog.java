package de.bauersoft.views.allergen;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.providers.AllergenDataProvider;
import de.bauersoft.services.AllergenService;
import de.bauersoft.views.DialogState;
import org.springframework.dao.DataIntegrityViolationException;

public class AllergenDialog extends Dialog
{

    private final FilterDataProvider<Allergen, Long> filterDataProvider;
    private final AllergenService allergenService;
    private final Allergen item;
    private final DialogState state;

    public AllergenDialog(FilterDataProvider<Allergen, Long> filterDataProvider, AllergenService allergenService, Allergen item, DialogState state)
    {
        this.filterDataProvider = filterDataProvider;
        this.allergenService = allergenService;
        this.item = item;
        this.state = state;

        this.setHeaderTitle(state.toString());

        Binder<Allergen> binder = new Binder<>(Allergen.class);

        FormLayout inputLayout = new FormLayout();
        inputLayout.setWidth("30rem");

        inputLayout.setResponsiveSteps(new ResponsiveStep("0", 1));

        TextField nameTextField = new TextField();
        nameTextField.setMaxLength(64);
        nameTextField.setRequired(true);
        nameTextField.setAutofocus(true);
        nameTextField.setWidthFull();

        TextArea descriptionTextArea = new TextArea();
        descriptionTextArea.setMaxLength(1024);
        descriptionTextArea.setSizeFull();
        descriptionTextArea.setMinHeight("calc(4* var(--lumo-text-field-size))");

        inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "Name"), 1);
        inputLayout.setColspan(inputLayout.addFormItem(descriptionTextArea, "Beschreibung"), 1);

        binder.forField(nameTextField).asRequired((value, context) ->
        {
            return (value != null && !value.isBlank())
                    ? ValidationResult.ok()
                    : ValidationResult.error("Name ist erforderlich");

        }).bind("name");

        binder.bind(descriptionTextArea, "description");

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
                    allergenService.update(item);
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
