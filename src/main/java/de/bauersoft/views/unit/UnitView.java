package de.bauersoft.views.unit;

import com.vaadin.flow.component.HtmlComponent;
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
import de.bauersoft.data.entities.ingredient.Ingredient;
import de.bauersoft.data.entities.unit.Unit;
import de.bauersoft.data.providers.UnitDataProvider;
import de.bauersoft.services.IngredientService;
import de.bauersoft.services.UnitService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@PageTitle("unit")
@Route(value = "unit", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UnitView extends Div {
	private final AutoFilterGrid<Unit> grid = new AutoFilterGrid<>(Unit.class, false, true);

	public UnitView(UnitService unitService,
					UnitDataProvider dataProvider,
					IngredientService ingredientService)
	{
		setClassName("content");

		grid.setWidthFull();
		grid.setHeightFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.addColumn("name");
		grid.addColumn("shorthand");
		grid.addColumn(unit -> (unit.getParentUnit() == null) ? "" : unit.getParentUnit().getName()).setHeader("Parent");
		grid.addColumn(unit -> (unit.getParentFactor() == 0.0f) ? "" : unit.getParentFactor()).setHeader("Faktor");

		grid.setItems(dataProvider);

		grid.addItemDoubleClickListener(event ->
		{
			new UnitDialog(unitService, dataProvider, event.getItem(), DialogState.EDIT);
		});

		GridContextMenu<Unit> contextMenu = grid.addContextMenu();
		contextMenu.addItem("new", event ->
		{
			new UnitDialog(unitService, dataProvider, new Unit(), DialogState.NEW);
		});

		GridMenuItem<Unit> deleteItem = contextMenu.addItem("delete", event ->
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

					div.add(new Text("Die Unit " + item.getName() + "(" + item.getShorthand() + ")" + " kann nicht gelöscht werden da sie von einigen Zutaten verwendet wird."));

					Notification notification = new Notification(div);
					notification.setDuration(5000);
					notification.setPosition(Notification.Position.MIDDLE);
					notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
					notification.open();
					return;
				}

				unitService.deleteById(item.getId());
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
