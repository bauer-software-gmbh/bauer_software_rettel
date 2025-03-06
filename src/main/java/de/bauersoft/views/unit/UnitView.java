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
import de.bauersoft.data.entities.unit.Unit;
import de.bauersoft.data.providers.UnitDataProvider;
import de.bauersoft.services.IngredientService;
import de.bauersoft.services.UnitService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import de.bauersoft.components.autofiltergrid.AutofilterGrid;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Einheiten")
@Route(value = "unit", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UnitView extends Div
{
    private final AutofilterGrid<Unit> grid;

    public UnitView(UnitService unitService,
                    IngredientService ingredientService)
    {
        setClassName("content");

        grid = new AutofilterGrid<>(unitService.getRepository());

        grid.setWidthFull();
        grid.setHeightFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn("name", "Name", Unit::getName);
        grid.addColumn("shorthand", "Abkürzung", Unit::getShorthand);

        grid.addColumn("parentUnit", "Parent", unit ->
        {
            return (unit.getParentUnit() == null) ? "" : unit.getParentUnit().getName();

        }, (s, unitRoot, path, criteriaQuery, criteriaBuilder) ->
		{
			return criteriaBuilder.like(path.get("name"), "%" + s + "%");
		});

        grid.addColumn("parentFactor", "Faktor", unit ->
        {
            return (unit.getParentFactor() == null) ? "" : unit.getParentFactor().toString();
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
                grid.refreshAll();
            });
        });

        contextMenu.addGridContextMenuOpenedListener(event ->
        {
            deleteItem.setVisible(event.getItem().isPresent());
        });
        this.add(grid);
    }

    public AutofilterGrid<Unit> getGrid()
    {
        return grid;
    }
}
