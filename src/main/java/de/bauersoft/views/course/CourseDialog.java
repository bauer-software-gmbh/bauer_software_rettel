package de.bauersoft.views.course;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.providers.CourseDataProvider;
import de.bauersoft.services.CourseService;
import de.bauersoft.views.DialogState;
import org.springframework.dao.DataIntegrityViolationException;

public class CourseDialog extends Dialog
{
	private final CourseService courseService;
	private final CourseDataProvider courseDataProvider;
	private final Course item;
	private final DialogState state;

	public CourseDialog(CourseService courseService, CourseDataProvider courseDataProvider, Course item, DialogState state)
	{
        this.courseService = courseService;
        this.courseDataProvider = courseDataProvider;
        this.item = item;
        this.state = state;

        this.setHeaderTitle(state.toString());

		Binder<Course> binder = new Binder<>(Course.class);

		FormLayout inputLayout = new FormLayout();
		inputLayout.setWidth("50vw");
		inputLayout.setMaxWidth("50em");
		inputLayout.setHeight("50vh");
		inputLayout.setMaxHeight("18em");

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

		}).bind(Course::getName, Course::setName);

		binder.setBean(item);

		Button saveButton = new Button("Speichern");
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
					courseService.update(binder.getBean());
					courseDataProvider.refreshAll();

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
			courseDataProvider.refreshAll();
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
