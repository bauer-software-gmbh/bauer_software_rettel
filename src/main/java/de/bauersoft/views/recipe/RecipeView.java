package de.bauersoft.views.recipe;


import java.util.stream.Collectors;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.Recipe;
import de.bauersoft.data.providers.RecipeDataProvider;
import de.bauersoft.data.repositories.component.ComponentRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.formulation.FormulationRepository;
import de.bauersoft.data.repositories.ingredient.IngredientRepository;
import de.bauersoft.data.repositories.pattern.PatternRepository;
import de.bauersoft.services.RecipeService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Recipe")
@Route(value = "recipe", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class RecipeView extends Div {
	private final AutoFilterGrid<Recipe> grid = new AutoFilterGrid<>(Recipe.class, false, true);


	public RecipeView(RecipeService recipeService, IngredientRepository ingredientRepository, FormulationRepository formulationRepository, PatternRepository patternRepository, RecipeDataProvider dataProvider, ComponentRepository componentRepository, CourseRepository courseRepository) {
		setClassName("content");
		grid.addColumn("name");
		grid.addColumn("description");
		grid.addColumn(item -> 
		item.getFormulation().stream().map(formulation -> formulation.getIngredient().getName()).collect(Collectors.joining(",")));
		grid.addItemDoubleClickListener(
				event -> new RecipeDialog(recipeService,ingredientRepository,formulationRepository,patternRepository, dataProvider, event.getItem(), DialogState.EDIT, componentRepository, courseRepository));
		grid.setDataProvider(dataProvider);
		GridContextMenu<Recipe> contextMenu = grid.addContextMenu();
		contextMenu.addItem("new recipe",
				event -> new RecipeDialog(recipeService,ingredientRepository,formulationRepository,patternRepository, dataProvider, new Recipe(), DialogState.NEW, componentRepository, courseRepository));
		contextMenu.addItem("delete", event -> event.getItem().ifPresent(item -> {
			
			recipeService.delete(item.getId());
			dataProvider.refreshAll();
		}));
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.setHeightFull();
		this.add(grid);
	}

	
}
