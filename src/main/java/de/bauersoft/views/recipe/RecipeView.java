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
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.components.autofiltergridOld.AutoFilterGrid;
import de.bauersoft.data.entities.formulation.Formulation;
import de.bauersoft.data.entities.ingredient.Ingredient;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.entities.recipe.Recipe;
import de.bauersoft.data.providers.RecipeDataProvider;
import de.bauersoft.services.*;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

import java.util.stream.Collectors;

@PageTitle("Rezepte")
@Route(value = "recipe", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "KITCHEN_ADMIN", "OFFICE_ADMIN"})
public class RecipeView extends Div
{
    private final RecipeService recipeService;
    private final IngredientService ingredientService;
    private final FormulationService formulationService;
    private final PatternService patternService;

    private final FilterDataProvider<Recipe, Long> filterDataProvider;
    private final AutofilterGrid<Recipe, Long> grid;

    public RecipeView(RecipeService recipeService,
                      IngredientService ingredientService,
                      FormulationService formulationService,
                      PatternService patternService,
                      ComponentService componentService)
    {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.formulationService = formulationService;
        this.patternService = patternService;

        setClassName("content");

        filterDataProvider = new FilterDataProvider<>(recipeService);
        grid = new AutofilterGrid<>(filterDataProvider);

        grid.setHeightFull();
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn("name", "Name", Recipe::getName, false);
        grid.addColumn("description", "Beschreibung", Recipe::getDescription, false);
        grid.addColumn("patterns", "Ernährungsformen", recipe ->
        {
            return recipe.getPatterns().stream().map(Pattern::getName).collect(Collectors.joining(", "));
        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            Join<Recipe, Pattern> patternJoin = root.join("patterns", JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(patternJoin.get("name")), "%" + filterInput.toLowerCase() + "%");
        }).enableSorting(false);

        grid.addColumn("formulations", "Zutaten", recipe ->
        {
            return recipe.getFormulations().stream().map(formulation -> formulation.getIngredient().getName()).collect(Collectors.joining(", "));

        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            Join<Recipe, Ingredient> formulationJoin = root.join("formulations").join("ingredient");
            return criteriaBuilder.like(criteriaBuilder.lower(formulationJoin.get("name").as(String.class)), filterInput + "%");
        }).enableSorting(false);

        grid.AutofilterGridContextMenu()
                        .enableGridContextMenu()
                        .enableAddItem("Neues Rezept", event ->
                        {
                            new RecipeDialog(filterDataProvider, recipeService, ingredientService, formulationService, patternService, new Recipe(), DialogState.NEW);

                        }).enableDeleteItem("Löschen", event ->
                        {
                            event.getItem().ifPresent(item ->
                            {
                                if(componentService.getRepository().existsByRecipesId(item.getId()))
                                {
                                    Div div = new Div();
                                    div.setMaxWidth("33vw");
                                    div.getStyle().set("white-space", "normal");
                                    div.getStyle().set("word-wrap", "break-word");

                                    div.add(new Text("Das Rezept \"" + item.getName() + "\" kann nicht gelöscht werden, da es noch von einigen Komponenten verwendet wird."));

                                    Notification notification = new Notification(div);
                                    notification.setDuration(5000);
                                    notification.setPosition(Notification.Position.MIDDLE);
                                    notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                                    notification.open();
                                    return;
                                }

                                formulationService.getRepository().deleteAllByRecipeId(item.getId());
                                recipeService.deleteById(item.getId());

                                filterDataProvider.refreshAll();
                            });
                        });

        grid.addItemDoubleClickListener(event ->
        {
            new RecipeDialog(filterDataProvider, recipeService, ingredientService, formulationService, patternService, event.getItem(), DialogState.EDIT);
        });

        this.add(grid);
    }
}
