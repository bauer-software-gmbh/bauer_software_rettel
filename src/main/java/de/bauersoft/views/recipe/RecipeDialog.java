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

public class RecipeDialog extends Dialog
{

	private final RecipeService recipeService;
	private final RecipeRepository recipeRepository;
	private final IngredientService ingredientService;
	private final IngredientRepository ingredientRepository;
	private final FormulationService formulationService;
	private final FormulationRepository formulationRepository;
	private final PatternService patternService;
	private final PatternRepository patternRepository;
	private final RecipeDataProvider recipeDataProvider;
	private final Recipe item;
	private final DialogState state;

	public RecipeDialog(RecipeService recipeService, IngredientService ingredientService,
						FormulationService formulationService, PatternService patternService,
						RecipeDataProvider recipeDataProvider, Recipe item, DialogState state)
	{
		this.recipeService = recipeService;
		this.ingredientService = ingredientService;
		this.formulationService = formulationService;
		this.patternService = patternService;
		this.recipeDataProvider = recipeDataProvider;
		this.item = item;
		this.state = state;

		this.recipeRepository = recipeService.getRepository();
		this.ingredientRepository = ingredientService.getRepository();
		this.formulationRepository = formulationService.getRepository();
		this.patternRepository = patternService.getRepository();

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

		binder.forField(nameTextField).asRequired((value, context) ->
		{
			return (value != null && !value.isBlank())
					? ValidationResult.ok()
					: ValidationResult.error("Name is required");

		}).bind("name");

		binder.bind(descriptionTextArea, "description");
		binder.bind(patternMultiSelectComboBox, "patterns");
		
		FormulationComponent formulationComponent = new FormulationComponent();
		formulationComponent.setFormulations(formulationRepository.findAllByRecipeId(item.getId()));
		formulationComponent.setIngredients(ingredientRepository.findAll());
		formulationComponent.updateView();
		formulationComponent.setHeight("50vh");

		binder.setBean(item);

		Button saveButton = new Button("save");
		saveButton.addClickShortcut(Key.ENTER);

		saveButton.setMinWidth("150px");
		saveButton.setMaxWidth("180px");

		saveButton.addClickListener(e ->
		{
			binder.validate();
			if(binder.isValid() && formulationComponent.isValid())
			{
				try
				{
					Recipe recipe = binder.getBean();
					recipeService.update(recipe);

					formulationComponent.accept(recipe);
					formulationService.updateFormulations(recipe.getFormulations().stream().toList(), formulationComponent.getFormulationsMap().keySet().stream().toList());

//					formulationRepository.deleteAllByRecipeId(recipe.getId());
//
//					formulationComponent.accept(recipe);
//					recipe.setFormulations(formulationComponent.getFormulationsMap().keySet());

//					recipeService.update(recipe);

					recipeDataProvider.refreshAll();

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
		cancelButton.setMaxWidth("180px");
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		cancelButton.addClickListener(e ->
		{
			binder.removeBean();
			this.close();
		});

		inputLayout.setWidth("50vw");
		inputLayout.setMaxWidth("50em");
		inputLayout.setHeight("50vh");
		inputLayout.setMaxHeight("16em");
		Span spacer = new Span();
		spacer.setWidthFull();
		this.add(inputLayout, formulationComponent);
		this.getFooter().add(new HorizontalLayout(spacer, saveButton, cancelButton));
		this.setCloseOnEsc(false);
		this.setCloseOnOutsideClick(false);
		this.setModal(true);
		this.open();
	}
}
