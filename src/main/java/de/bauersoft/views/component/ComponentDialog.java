package de.bauersoft.views.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import de.bauersoft.data.entities.Component;
import de.bauersoft.data.entities.Course;
import de.bauersoft.data.entities.Recipe;
import de.bauersoft.data.providers.ComponentDataProvider;
import de.bauersoft.data.repositories.component.ComponentRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.recipe.RecipeRepository;
import de.bauersoft.services.ComponentService;
import de.bauersoft.views.DialogState;

public class ComponentDialog extends Dialog {
	public ComponentDialog(ComponentService service, ComponentDataProvider provider, RecipeRepository recipeRepository,
			CourseRepository courseRepository, ComponentRepository componentRepository, Component item,
			DialogState state) {
		Binder<Component> binder = new Binder<Component>(Component.class);
		this.setHeaderTitle(state.toString());
		FormLayout inputLayout = new FormLayout();
		inputLayout.setResponsiveSteps(new ResponsiveStep("0", 1));
		TextField nameTextField = new TextField();
		nameTextField.setMaxLength(64);
		nameTextField.setRequired(true);
		nameTextField.setMinWidth("20em");
		TextArea descriptionTextArea = new TextArea();
		descriptionTextArea.setMaxLength(512);
		descriptionTextArea.setSizeFull();
		descriptionTextArea.setMinHeight("calc(4* var(--lumo-text-field-size))");
		ComboBox<Course> courseComboBox = new ComboBox<Course>();
		courseComboBox.setItemLabelGenerator(course -> course.getName());
		courseComboBox.setItems(courseRepository.findAll());
		courseComboBox.setMinWidth("20em");
		courseComboBox.setRequired(true);
		MultiSelectComboBox<Recipe> recipeMultiSelectComboBox = new MultiSelectComboBox<Recipe>();
		recipeMultiSelectComboBox.setItemLabelGenerator(recipe -> recipe.getName());
		recipeMultiSelectComboBox.setItems(recipeRepository.findAll());
		recipeMultiSelectComboBox.setWidthFull();
		inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "name"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(descriptionTextArea, "description"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(courseComboBox, "course"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(recipeMultiSelectComboBox, "recipe"), 1);
		binder.bind(nameTextField, "name");
		binder.bind(descriptionTextArea, "name");
		binder.bind(courseComboBox, "course");
		binder.bind(recipeMultiSelectComboBox, "recipes");
		binder.setBean(item);
		Button saveButton = new Button("save");
		saveButton.setMinWidth("150px");
		saveButton.setMaxWidth("180px");
		saveButton.addClickListener(e -> {
			if (binder.isValid()) {
				service.update(binder.getBean());
				provider.refreshAll();
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
		inputLayout.setMaxHeight("20em");
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
