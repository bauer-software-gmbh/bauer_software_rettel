package de.bauersoft.views.field;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.providers.FieldDataProvider;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.field.FieldMultiplierRepository;
import de.bauersoft.services.CourseService;
import de.bauersoft.services.FieldMultiplierService;
import de.bauersoft.services.FieldService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.field.multiplier.MultiplierComponent;
import lombok.Getter;
import org.springframework.dao.DataIntegrityViolationException;

@Getter
public class FieldDialog extends Dialog
{

    private FieldService fieldService;
    private FieldDataProvider fieldDataProvider;
    private FieldMultiplierService fieldMultiplierService;
    private FieldMultiplierRepository fieldMultiplierRepository;
    private CourseService courseService;
    private CourseRepository courseRepository;

    private Field item;

    private DialogState state;

    public FieldDialog(FieldService fieldService, FieldDataProvider fieldDataProvider, FieldMultiplierService fieldMultiplierService, CourseService courseService, Field item, DialogState state)
    {
        this.fieldService = fieldService;
        this.fieldDataProvider = fieldDataProvider;
        this.fieldMultiplierService = fieldMultiplierService;
        this.fieldMultiplierRepository = fieldMultiplierService.getRepository();
        this.courseService = courseService;
        this.courseRepository = courseService.getRepository();
        this.item = item;
        this.state = state;

		this.setHeaderTitle(state.toString());

        Binder<Field> binder = new Binder<>(Field.class);

        FormLayout inputLayout = new FormLayout();
        inputLayout.setWidth("50vw");
        inputLayout.setMaxWidth("50em");

        inputLayout.setResponsiveSteps(new ResponsiveStep("0", 1));

        TextField nameTextField = new TextField();
        nameTextField.setMaxLength(64);
        nameTextField.setAutofocus(true);
        nameTextField.setRequired(true);
        nameTextField.setMinWidth("20em");

        inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "Name"), 1);

        binder.forField(nameTextField).asRequired((value, context) ->
		{
			return (value != null && !value.isBlank())
					? ValidationResult.ok()
					: ValidationResult.error("Name ist erforderlich");
		}).bind("name");

        binder.readBean(item);

        MultiplierComponent multiplierComponent = new MultiplierComponent(this);

        Button saveButton = new Button("Speichern");
		saveButton.addClickShortcut(Key.ENTER);
        saveButton.setMinWidth("150px");
        saveButton.setMaxWidth("180px");
        saveButton.addClickListener(e ->
        {
            multiplierComponent.getMultiplierMapContainer().acceptTemporaries();

            try
            {
                binder.writeBean(item);
                fieldService.update(item);

                multiplierComponent.getMultiplierMapContainer().evaluate(container ->
                {
                    container.getEntity().getId().setFieldId(item.getId());
                });

                multiplierComponent.getMultiplierMapContainer().run(fieldMultiplierService);

                fieldDataProvider.refreshAll();

                Notification.show("Daten wurden aktualisiert");
                this.close();

            }catch(DataIntegrityViolationException error)
            {
                Notification.show("Doppelter Eintrag", 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);

            }catch(ValidationException err)
            {
            }
        });

        Button cancelButton = new Button("Abbrechen");
		cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.setMinWidth("150px");
        cancelButton.setMaxWidth("200px");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(e ->
        {
            multiplierComponent.getMultiplierMapContainer().loadTemporaries();
            fieldDataProvider.refreshAll();
            this.close();
        });

        this.add(inputLayout, multiplierComponent);
        this.getFooter().add(saveButton, cancelButton);
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);
        this.setModal(true);
        this.open();
    }

}
