package de.bauersoft.views.component;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import de.bauersoft.data.entities.component.Component;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.recipe.Recipe;
import de.bauersoft.data.entities.unit.Unit;
import de.bauersoft.data.providers.ComponentDataProvider;
import de.bauersoft.data.repositories.component.ComponentRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.recipe.RecipeRepository;
import de.bauersoft.services.ComponentService;
import de.bauersoft.services.UnitService;
import de.bauersoft.views.DialogState;
import org.springframework.dao.DataIntegrityViolationException;

public class ComponentDialog extends Dialog
{
	private final ComponentService componentService;
	private final ComponentDataProvider componentDataProvider;
	private final RecipeRepository recipeRepository;
	private final CourseRepository courseRepository;
	private final ComponentRepository componentRepository;
	private final Component item;
	private final DialogState state;

	public ComponentDialog(ComponentService componentService, ComponentDataProvider componentDataProvider, RecipeRepository recipeRepository, CourseRepository courseRepository, ComponentRepository componentRepository, Component item, DialogState state)
	{
        this.componentService = componentService;
        this.componentDataProvider = componentDataProvider;
        this.recipeRepository = recipeRepository;
        this.courseRepository = courseRepository;
        this.componentRepository = componentRepository;
        this.item = item;
        this.state = state;

		this.setHeaderTitle(state.toString());

		Binder<Component> binder = new Binder<>(Component.class);

		FormLayout inputLayout = new FormLayout();
		inputLayout.setWidth("50vw");
		inputLayout.setMaxWidth("50em");
		inputLayout.setHeight("50vh");
		inputLayout.setMaxHeight("20em");
		inputLayout.setResponsiveSteps(new ResponsiveStep("0", 1));

		TextField nameTextField = new TextField();
		nameTextField.setMaxLength(64);
		nameTextField.setAutofocus(true);
		nameTextField.setRequired(true);
		nameTextField.setMinWidth("20em");

		TextArea descriptionTextArea = new TextArea();
		descriptionTextArea.setMaxLength(1024);
		descriptionTextArea.setSizeFull();
		descriptionTextArea.setMinHeight("calc(4* var(--lumo-text-field-size))");

		ComboBox<Course> courseComboBox = new ComboBox<>("Menükomponente");
		courseComboBox.setItemLabelGenerator(course -> course.getName());
		courseComboBox.setItems(courseRepository.findAll());
		courseComboBox.setWidthFull();
		courseComboBox.setRequired(true);

		ComboBox<Unit> unitComboBox = new ComboBox<>("Einheit");
		unitComboBox.setItemLabelGenerator(unit -> unit.getName());
		unitComboBox.setItems(unitService.findAll());
		unitComboBox.setWidthFull();
		unitComboBox.setRequired(true);

		HorizontalLayout comboBoxLayout = new HorizontalLayout();
		comboBoxLayout.setWidthFull();
		comboBoxLayout.add(courseComboBox, unitComboBox);

		MultiSelectComboBox<Recipe> recipeMultiSelectComboBox = new MultiSelectComboBox<>();
		recipeMultiSelectComboBox.setWidthFull();

		recipeMultiSelectComboBox.setItems(recipeRepository.findAll());
		recipeMultiSelectComboBox.setItemLabelGenerator(recipe -> recipe.getName());


		inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "Name"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(descriptionTextArea, "Beschreibung"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(comboBoxLayout, "Eigenschaften"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(recipeMultiSelectComboBox, "Rezept"), 1);

		binder.forField(nameTextField).asRequired((value, context) ->
		{
			return (value != null && !value.isBlank())
					? ValidationResult.ok()
					: ValidationResult.error("Name it erforderlich");

		}).bind(Component::getName, Component::setName);

		binder.bind(descriptionTextArea, "name");

		binder.forField(courseComboBox).asRequired((value, context) ->
		{
			return (value != null)
					? ValidationResult.ok()
					: ValidationResult.error("Gang ist erforderlich");

		}).bind(Component::getCourse, Component::setCourse);

		binder.forField(unitComboBox).asRequired((value, context) ->
		{
			return (value != null)
					? ValidationResult.ok()
					: ValidationResult.error("Einheit ist erforderlich");

		}).bind(Component::getUnit, Component::setUnit);

		binder.bind(recipeMultiSelectComboBox, "recipes");


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
					componentService.update(binder.getBean());
					componentDataProvider.refreshAll();

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
			componentDataProvider.refreshAll();
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
