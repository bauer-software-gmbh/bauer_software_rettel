package de.bauersoft.views.incredient;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.ingredient.Ingredient;
import de.bauersoft.data.providers.IngredientDataProvider;
import de.bauersoft.data.repositories.additive.AdditiveRepository;
import de.bauersoft.data.repositories.allergen.AllergenRepository;
import de.bauersoft.data.repositories.unit.UnitRepository;
import de.bauersoft.services.IngredientService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

import java.util.stream.Collectors;


@PageTitle("Zutaten")
@Route(value = "incredient", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class IngredientView extends Div
{
    private final AutoFilterGrid<Ingredient> grid = new AutoFilterGrid<>(Ingredient.class, false, true);

    public IngredientView(IngredientService service, UnitRepository unitRepository, AllergenRepository allergenRepository, AdditiveRepository additiveRepository, IngredientDataProvider dataProvider)
    {
        setClassName("content");

        grid.setDataProvider(dataProvider);

        grid.setHeightFull();
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn("name").setResizable(true);
        grid.addColumn("description").setResizable(true);

        //grid.addColumn(item -> item.getUnit() == null ? "" : item.getUnit().getName()).setKey("unit.name").setHeader("unit").setResizable(true);
        grid.addColumn("unit.name").setHeader("Unit").setResizable(true);

        grid.addComponentColumn(item ->
        {
            if(item.getAllergens() == null || item.getAllergens().isEmpty())
                return new Span();

            Icon icon = VaadinIcon.WARNING.create();
            icon.setTooltipText(item.getAllergens().stream().map(Allergen::getName).collect(Collectors.joining(",")));

            return icon;
        }).setKey("allergens.name").setHeader("Allergens");

        grid.addComponentColumn(item ->
        {
            if(item.getAdditives() == null || item.getAdditives().isEmpty())
                return new Span();

            Icon icon = VaadinIcon.WARNING.create();
            icon.setTooltipText(item.getAdditives().stream().map(Additive::getName).collect(Collectors.joining(",")));

            return icon;
        }).setKey("additives.name").setHeader("Additives").setResizable(true);

        grid.addItemDoubleClickListener(event ->
        {
            new IngredientDialog(service, unitRepository, allergenRepository, additiveRepository, dataProvider, event.getItem(), DialogState.EDIT);
        });

        GridContextMenu<Ingredient> contextMenu = grid.addContextMenu();
        contextMenu.addItem("new ingredient", event ->
        {
            new IngredientDialog(service, unitRepository, allergenRepository, additiveRepository, dataProvider, new Ingredient(), DialogState.NEW);
        });

        GridMenuItem<Ingredient> deleteItem = contextMenu.addItem("delete", event ->
        {
            event.getItem().ifPresent(item ->
            {
                service.delete(item.getId());
                dataProvider.refreshAll();
            });
        });

		contextMenu.addGridContextMenuOpenedListener(event ->
		{
			deleteItem.setVisible(event.getItem().isPresent());
		});

        this.add(grid);
    }
}
