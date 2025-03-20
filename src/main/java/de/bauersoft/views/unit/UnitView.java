package de.bauersoft.views.unit;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofilter.Filter;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.data.entities.unit.Unit;
import de.bauersoft.services.IngredientService;
import de.bauersoft.services.UnitService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.Arrays;
import java.util.Collections;

@PageTitle("Einheiten")
@Route(value = "unit", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UnitView extends Div
{
    private final FilterDataProvider<Unit, Long> filterDataProvider;

    private final AutofilterGrid<Unit, Long> grid;

    public UnitView(UnitService unitService,
                    IngredientService ingredientService)
    {
        setClassName("content");

        filterDataProvider = new FilterDataProvider<Unit, Long>(unitService);

        grid = new AutofilterGrid<>(filterDataProvider);
        grid.setWidthFull();
        grid.setHeightFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addFilter(new Filter<Unit>("name", (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            criteriaQuery.orderBy(criteriaBuilder.asc(path.as(String.class)));
            return criteriaBuilder.conjunction();
        }).setIgnoreFilterInput(true));

        grid.addColumn("Name", Unit::getName, root -> root.get("name"), (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.like(path.as(String.class), "%" + filterInput + "%");
        });

        grid.addColumn("shorthand", "Abkürzung", Unit::getShorthand, s -> "%" + s + "%", false);

        grid.addColumn("parentUnit", "Parent", unit ->
        {
            return (unit.getParentUnit() == null) ? "" : unit.getParentUnit().getName();

        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.like(path.get("name"), "%" + filterInput + "%");
        });

        grid.addColumn("parentFactor", "Faktor", unit ->
        {
            return (unit.getParentFactor() == null) ? "" : unit.getParentFactor().toString();
        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.like(path.as(String.class), filterInput + "%");
        });

        // Sortiere die Spalte "name" aufsteigend
        //grid.sort(GridSortOrder.asc(grid.getColumnByKey("name")).build());

        grid.addItemDoubleClickListener(event ->
        {
            new UnitDialog(filterDataProvider, unitService, event.getItem(), DialogState.EDIT);
        });

        GridContextMenu<Unit> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Neue Einheit", event ->
        {
            new UnitDialog(filterDataProvider, unitService, new Unit(), DialogState.NEW);
        });

        GridMenuItem<Unit> deleteItem = contextMenu.addItem("Löschen", event ->
        {
            event.getItem().ifPresent(item ->
            {
                if(ingredientService.getRepository().existsByUnitId(item.getId()))
                {
                    //TODO später durch fancy dialog ersetzen :3
                    Div div = new Div();
                    div.setMaxWidth("33vw");
                    div.getStyle().set("white-space", "normal");
                    div.getStyle().set("word-wrap", "break-word");

                    div.add(new Text("Die Einheit " + item.getName() + "(" + item.getShorthand() + ")" + " kann nicht gelöscht werden, da sie von einigen Zutaten verwendet wird."));

                    Notification notification = new Notification(div);
                    notification.setDuration(5000);
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                    notification.open();
                    return;
                }

                unitService.deleteById(item.getId());
                filterDataProvider.refreshAll();
            });
        });

        contextMenu.addGridContextMenuOpenedListener(event ->
        {
            deleteItem.setVisible(event.getItem().isPresent());
        });

        this.add(grid);
    }

    public FilterDataProvider<Unit, Long> getFilterDataProvider()
    {
        return filterDataProvider;
    }
}