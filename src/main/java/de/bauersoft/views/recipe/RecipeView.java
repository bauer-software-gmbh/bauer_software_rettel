package de.bauersoft.views.recipe;


import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.formulation.Formulation;
import de.bauersoft.data.entities.recipe.Recipe;
import de.bauersoft.data.providers.RecipeDataProvider;
import de.bauersoft.data.repositories.formulation.FormulationRepository;
import de.bauersoft.data.repositories.ingredient.IngredientRepository;
import de.bauersoft.data.repositories.pattern.PatternRepository;
import de.bauersoft.services.*;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@PageTitle("Recipe")
@Route(value = "recipe", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class RecipeView extends Div
{
    private final AutoFilterGrid<Recipe> grid = new AutoFilterGrid<>(Recipe.class, false, true);

    private final RecipeService recipeService;
    private final IngredientService ingredientService;
    private final FormulationService formulationService;
    private final PatternService patternService;
    private final RecipeDataProvider recipeDataProvider;

    public RecipeView(RecipeService recipeService, IngredientService ingredientService, FormulationService formulationService,
                      PatternService patternService, RecipeDataProvider recipeDataProvider, ComponentService componentService)
    {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.formulationService = formulationService;
        this.patternService = patternService;
        this.recipeDataProvider = recipeDataProvider;

        setClassName("content");

        grid.setHeightFull();
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.setDataProvider(recipeDataProvider);

        grid.addColumn("name");
        grid.addColumn("description");

        grid.addColumn(item ->
        {
            return item.getFormulations().stream().map(formulation -> formulation.getIngredient().getName()).collect(Collectors.joining(","));
        });

        grid.addItemDoubleClickListener(event ->
        {
            new RecipeDialog(recipeService, ingredientService, formulationService, patternService, recipeDataProvider, event.getItem(), DialogState.EDIT);
        });

        GridContextMenu<Recipe> contextMenu = grid.addContextMenu();
        contextMenu.addItem("new recipe", event ->
        {
			new RecipeDialog(recipeService, ingredientService, formulationService, patternService, recipeDataProvider, new Recipe(), DialogState.EDIT);
        });

        GridMenuItem<Recipe> deleteItem = contextMenu.addItem("delete", event ->
        {
            event.getItem().ifPresent(item ->
            {
                if(componentService.getRepository().existsByRecipesId(item.getId()))
                {
                    Div div = new Div();
                    div.setMaxWidth("33vw");
                    div.getStyle().set("white-space", "normal");
                    div.getStyle().set("word-wrap", "break-word");

                    div.add(new Text("Das Rezept \"" + item.getName() + "\" kann nicht gelöscht werden da es noch von einigen Komponenten verwendet wird."));

                    Notification notification = new Notification(div);
                    notification.setDuration(5000);
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                    notification.open();
                    return;
                }

                formulationService.getRepository().deleteAllByRecipeId(item.getId());
                recipeService.delete(item.getId());

                recipeDataProvider.refreshAll();
            });
        });

        contextMenu.addGridContextMenuOpenedListener(event ->
        {
            deleteItem.setVisible(event.getItem().isPresent());
        });

        this.add(grid);
    }


}
