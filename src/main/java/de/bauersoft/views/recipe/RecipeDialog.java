package de.bauersoft.views.recipe;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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

import de.bauersoft.data.entities.Formulation;
import de.bauersoft.data.entities.Pattern;

import de.bauersoft.data.entities.Recipe;
import de.bauersoft.data.providers.RecipeDataProvider;
import de.bauersoft.data.repositories.formulation.FormulationRepository;
import de.bauersoft.data.repositories.ingredient.IngredientRepository;
import de.bauersoft.data.repositories.pattern.PatternRepository;
import de.bauersoft.services.RecipeService;
import de.bauersoft.views.DialogState;

public class RecipeDialog extends Dialog {
	public RecipeDialog(RecipeService service, IngredientRepository ingredientRepository,
			FormulationRepository formulationRepository, PatternRepository patternRepository,
			RecipeDataProvider provider, Recipe item, DialogState state) {
		this.setHeaderTitle(state.toString());
		Binder<Recipe> binder = new Binder<Recipe>(Recipe.class);
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
		descriptionTextArea.setWidthFull();
		MultiSelectComboBox<Pattern> patternMultiSelectComboBox = new MultiSelectComboBox<Pattern>();
		patternMultiSelectComboBox.setItemLabelGenerator(pattern -> pattern.getName());
		patternMultiSelectComboBox.setItems(patternRepository.findAll());
		patternMultiSelectComboBox.setWidthFull();
		inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "name"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(descriptionTextArea, "description"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(patternMultiSelectComboBox, "patterns"), 1);
		binder.bind(nameTextField, "name");
		binder.bind(descriptionTextArea, "description");
		binder.bind(patternMultiSelectComboBox, "patterns");
		
		FormulationComponent formulationComponent = new FormulationComponent(item);
		formulationComponent.setItems(ingredientRepository.findAll());
		formulationComponent.setValues(item.getFormulation());
		formulationComponent.setHeight("50vh");
		binder.setBean(item);
		Button saveButton = new Button("save");
		saveButton.setMinWidth("150px");
		saveButton.setMaxWidth("180px");
		saveButton.addClickListener(e -> {
			if (binder.isValid()) {
				Set<Formulation> oldFormulations = binder.getBean().getFormulation();
				formulationComponent.accept(binder.getBean());
				updateFormulations(oldFormulations,formulationComponent.getFormulations(),formulationRepository) ;
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
		inputLayout.setMaxHeight("16em");
		Span spacer = new Span();
		spacer.setWidthFull();
		this.add(inputLayout,formulationComponent);
		this.getFooter().add(new HorizontalLayout(spacer, saveButton, cancelButton));
		this.setCloseOnEsc(false);
		this.setCloseOnOutsideClick(false);
		this.setModal(true);
		this.open();
	}

	public Set<Formulation> updateFormulations(Collection<Formulation> oldValues, Collection<Formulation> newValues,
			FormulationRepository formulationRepository) {
		Set<Formulation> formulations = new HashSet<Formulation>();
		Set<Formulation> tmp = new HashSet<Formulation>();
		if (oldValues != null) {
			formulations.addAll(oldValues);
		}
		formulations.removeAll(newValues);
		for(Formulation formulation :formulations) {
			formulationRepository.findById(formulation.getId()).ifPresent(item->
				formulationRepository.deleteAllByRecipeId(item.getId().getRecipeId())
			);
		}
		for (Formulation formulation : newValues) {
			tmp.add(formulationRepository.save(formulation));
		}	
		formulations = tmp;
		return formulations;
	}
}
