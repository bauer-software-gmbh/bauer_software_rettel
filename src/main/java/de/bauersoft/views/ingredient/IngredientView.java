package de.bauersoft.views.ingredient;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.components.autofilter.grid.SortType;
import de.bauersoft.components.autofiltergridOld.AutoFilterGrid;
import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.ingredient.Ingredient;
import de.bauersoft.data.providers.IngredientDataProvider;
import de.bauersoft.data.repositories.additive.AdditiveRepository;
import de.bauersoft.data.repositories.allergen.AllergenRepository;
import de.bauersoft.data.repositories.unit.UnitRepository;
import de.bauersoft.services.AdditiveService;
import de.bauersoft.services.AllergenService;
import de.bauersoft.services.IngredientService;
import de.bauersoft.services.UnitService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

import java.util.stream.Collectors;

@PageTitle("Zutaten")
@Route(value = "incredient", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "KITCHEN_ADMIN", "OFFICE_ADMIN"})
public class IngredientView extends Div
{
    private final IngredientService ingredientService;
    private final UnitService unitService;
    private final AllergenService allergenService;
    private final AdditiveService additiveService;

    private final FilterDataProvider<Ingredient, Long> filterDataProvider;
    private final AutofilterGrid<Ingredient, Long> grid;

    public IngredientView(IngredientService ingredientService, UnitService unitService, AllergenService allergenService, AdditiveService additiveService)
    {
        this.ingredientService = ingredientService;
        this.unitService = unitService;
        this.allergenService = allergenService;
        this.additiveService = additiveService;

        setClassName("content");

        filterDataProvider = new FilterDataProvider<>(ingredientService);
        grid = new AutofilterGrid<>(filterDataProvider);

        grid.setHeightFull();
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn("name", "Name", Ingredient::getName, false);
        grid.addColumn("description", "Beschreibung", Ingredient::getDescription, false);

        grid.addColumn("unit", "Einheit", ingredient -> ingredient.getUnit().getName(), (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.like(criteriaBuilder.lower(path.get("name").as(String.class)), filterInput + "%");
        }, (root, path, criteriaQuery, criteriaBuilder, parent, sortOrder) ->
        {
            Join<Object, Object> join = root.join("unit", JoinType.LEFT);
            switch(sortOrder)
            {
                case ASCENDING:
                    return criteriaBuilder.asc(join.get("name"));
                case DESCENDING:
                    return criteriaBuilder.desc(join.get("name"));
                default:
                    return null;
            }
        }, SortType.ALPHA);

        grid.addColumn("allergens", "Allergene", ingredient ->
        {
           return ingredient.getAllergens().stream().map(Allergen::getName).collect(Collectors.joining(", ")).trim();

        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            Join<Ingredient, Allergen> allergenJoin = root.join("allergens");
            return criteriaBuilder.like(criteriaBuilder.lower(allergenJoin.get("name")), filterInput.toLowerCase() + "%");

        }).enableSorting(false);

        grid.addColumn("additives", "Zusatzstoffe", ingredient ->
        {
            return ingredient.getAdditives().stream().map(Additive::getName).collect(Collectors.joining(", ")).trim();

        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            Join<Ingredient, Additive> additiveJoin = root.join("additives");
            return criteriaBuilder.like(criteriaBuilder.lower(additiveJoin.get("name")), filterInput.toLowerCase() + "%");
        }).enableSorting(false);


//        grid.addComponentColumn(item ->
//        {
//            if(item.getAllergens() == null || item.getAllergens().isEmpty())
//                return new Span();
//
//            Icon icon = VaadinIcon.WARNING.create();
//            icon.setTooltipText(item.getAllergens().stream().map(Allergen::getName).collect(Collectors.joining(",")));
//
//            return icon;
//        }).setKey("allergens.name").setHeader("Allergene");
//
//        grid.addComponentColumn(item ->
//        {
//            if(item.getAdditives() == null || item.getAdditives().isEmpty())
//                return new Span();
//
//            Icon icon = VaadinIcon.WARNING.create();
//            icon.setTooltipText(item.getAdditives().stream().map(Additive::getName).collect(Collectors.joining(",")));
//
//            return icon;
//        }).setKey("additives.name").setHeader("Zusatzstoffe").setResizable(true);


        grid.AutofilterGridContextMenu()
                .enableGridContextMenu()
                .enableAddItem("Neue Zutat", event ->
                {
                    new IngredientDialog(filterDataProvider, ingredientService, unitService, allergenService, additiveService, new Ingredient(), DialogState.NEW);
                }).enableDeleteItem("Löschen", event ->
                {
                    event.getItem().ifPresent(item ->
                    {
                        ingredientService.deleteById(item.getId());
                        filterDataProvider.refreshAll();
                    });
                });

        grid.addItemDoubleClickListener(event ->
        {
            new IngredientDialog(filterDataProvider, ingredientService, unitService, allergenService, additiveService, event.getItem(), DialogState.EDIT);
        });

        this.add(grid);

    }
}
