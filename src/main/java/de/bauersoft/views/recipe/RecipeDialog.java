package de.bauersoft.views.recipe;


import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
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
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.recipe.Recipe;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.providers.RecipeDataProvider;
import de.bauersoft.data.repositories.formulation.FormulationRepository;
import de.bauersoft.data.repositories.ingredient.IngredientRepository;
import de.bauersoft.data.repositories.pattern.PatternRepository;
import de.bauersoft.data.repositories.recipe.RecipeRepository;
import de.bauersoft.services.FormulationService;
import de.bauersoft.services.IngredientService;
import de.bauersoft.services.PatternService;
import de.bauersoft.services.RecipeService;
import de.bauersoft.views.DialogState;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Comparator;

public class RecipeDialog extends Dialog
{
	private final FilterDataProvider<Recipe, Long> filterDataProvider;
	private final RecipeService recipeService;
	private final IngredientService ingredientService;
	private final FormulationService formulationService;
	private final PatternService patternService;
	private final Recipe item;
	private final DialogState state;

	public RecipeDialog(FilterDataProvider<Recipe, Long> filterDataProvider,
						RecipeService recipeService,
						IngredientService ingredientService,
                        FormulationService formulationService,
						PatternService patternService,
                        Recipe item,
						DialogState state)
	{
        this.filterDataProvider = filterDataProvider;
        this.recipeService = recipeService;
		this.ingredientService = ingredientService;
		this.formulationService = formulationService;
		this.patternService = patternService;
		this.item = item;
		this.state = state;

		this.setHeaderTitle(state.toString());

		Binder<Recipe> binder = new Binder<>(Recipe.class);

		FormLayout inputLayout = new FormLayout();
		inputLayout.setWidth("50vw");
		inputLayout.setMaxWidth("50em");
		inputLayout.setHeight("50vh");
		inputLayout.setMaxHeight("16em");
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
		descriptionTextArea.setWidthFull();

		MultiSelectComboBox<Pattern> patternMultiSelectComboBox = new MultiSelectComboBox<>();
		patternMultiSelectComboBox.setItems(query ->
						FilterDataProvider.lazyStream(patternService, query)
								.sorted(Comparator.comparing(Pattern::getName, String.CASE_INSENSITIVE_ORDER)),
				query -> (int) patternService.count()
		);
		patternMultiSelectComboBox.setItemLabelGenerator(Pattern::getName);

		patternMultiSelectComboBox.setWidthFull();

		inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "Name"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(descriptionTextArea, "Beschreibung"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(patternMultiSelectComboBox, "Ernährungsform"), 1);

		binder.forField(nameTextField).asRequired((value, context) ->
		{
			return (value != null && !value.isBlank())
					? ValidationResult.ok()
					: ValidationResult.error("Name ist erforderlich");

		}).bind("name");

		binder.bind(descriptionTextArea, "description");
		binder.forField(patternMultiSelectComboBox).asRequired((value, context) ->
		{
			return (value.size() >= 1)
					? ValidationResult.ok()
					: ValidationResult.error("Eine Ernährungsform muss angegeben werden.");
		}).bind(Recipe::getPatterns, Recipe::setPatterns);
		
		FormulationComponent formulationComponent = new FormulationComponent();
		formulationComponent.setFormulations(
				formulationService.findAllByRecipeId(item.getId())
						.stream()
						.sorted(Comparator.comparing(f -> f.getIngredient().getName().toLowerCase()))
						.toList()
		);
		formulationComponent.setIngredients(
				ingredientService.findAll()
						.stream()
						.sorted(Comparator.comparing(i -> i.getName().toLowerCase()))
						.toList()
		);
		formulationComponent.updateView();
		formulationComponent.setHeight("50vh");

		binder.readBean(item);

		Button saveButton = new Button("Speichern");
		saveButton.addClickShortcut(Key.ENTER);
		saveButton.setMinWidth("150px");
		saveButton.setMaxWidth("180px");
		saveButton.addClickListener(e ->
		{
			binder.writeBeanIfValid(item);;
			if(binder.isValid() && formulationComponent.isValid())
			{
				try
				{
					recipeService.update(item);

					formulationComponent.accept(item);
					formulationService.updateFormulations(item.getFormulations().stream().toList(), formulationComponent.getFormulationsMap().keySet().stream().toList());

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
		cancelButton.setMaxWidth("180px");
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		cancelButton.addClickListener(e ->
		{
			binder.removeBean();
			filterDataProvider.refreshAll();
			this.close();
		});

		this.add(inputLayout, formulationComponent);
		this.getFooter().add(saveButton, cancelButton);
		this.setCloseOnEsc(false);
		this.setCloseOnOutsideClick(false);
		this.setModal(true);
		this.open();
	}
}
