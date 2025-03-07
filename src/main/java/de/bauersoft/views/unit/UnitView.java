package de.bauersoft.views.unit;

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
import de.bauersoft.data.entities.unit.Unit;
import de.bauersoft.data.providers.UnitDataProvider;
import de.bauersoft.services.IngredientService;
import de.bauersoft.services.UnitService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Einheiten")
@Route(value = "unit", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UnitView extends Div
{
    private final FilterDataProvider<Unit, Long> filterDataProvider;

    private final AutofilterGrid<Unit, Long> grid;

    public UnitView(UnitService unitService,
                    IngredientService ingredientService,
                    UnitDataProvider unitDataProvider)
    {
        setClassName("content");

        filterDataProvider = new FilterDataProvider<>(unitService);

        grid = new AutofilterGrid<>(filterDataProvider);

        grid.setWidthFull();
        grid.setHeightFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn("name", "Name", Unit::getName);
        grid.addColumn("shorthand", "Abkürzung",
                Unit::getShorthand,
                (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
                {
                    return criteriaBuilder.like(path.as(String.class), filterInput);
                });

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


        grid.addItemDoubleClickListener(event ->
        {
            new UnitDialog(this, unitService, event.getItem(), DialogState.EDIT);
        });

        GridContextMenu<Unit> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Neue Einheit", event ->
        {
            new UnitDialog(this, unitService, new Unit(), DialogState.NEW);
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
