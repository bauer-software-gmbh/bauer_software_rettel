package de.bauersoft.views.unit;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.Unit;
import de.bauersoft.data.providers.UnitDataProvider;

import de.bauersoft.services.UnitService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("unit")
@Route(value = "unit", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UnitView extends Div {
	private final AutoFilterGrid<Unit> grid = new AutoFilterGrid<>(Unit.class, false, true);

	public UnitView(UnitService unitService, UnitDataProvider dataProvider) {
		setClassName("content");
		grid.addColumn("name");
		grid.addColumn("shorthand");
		grid.addColumn(item -> item.getParent() != null ? item.getParent().getName() : "").setKey("parent.name")
				.setHeader("Parent");
		grid.addColumn(item -> item.getParent_factor() != 0.0f ? item.getParent_factor() : "").setKey("parent.factor")
				.setHeader("Parent_factor");
		grid.setItems(dataProvider);
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		grid.setWidthFull();
		grid.addItemDoubleClickListener(
				event -> new UnitDialog(unitService, dataProvider, event.getItem(), DialogState.EDIT));
		GridContextMenu<Unit> contextMenu = grid.addContextMenu();
		contextMenu.addItem("new", event -> new UnitDialog(unitService, dataProvider, new Unit(), DialogState.NEW));
		contextMenu.addItem("delete", event -> {
			event.getItem().ifPresent(item -> {
				unitService.delete(item.getId());
			});
		});
		this.add(grid);
	}
}
