package de.bauersoft.views.additive;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.Additive;
import de.bauersoft.data.providers.AdditiveDataProvider;
import de.bauersoft.services.AdditiveService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Additive")
@Route(value = "additive", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class AdditiveView extends Div {

	private final AutoFilterGrid<Additive> grid = new AutoFilterGrid<>(Additive.class, false, true);
	public AdditiveView(AdditiveService service, AdditiveDataProvider dataProvider) {
		setClassName("content");
		grid.addColumn("name");
		grid.addColumn("description");
		grid.setItems(dataProvider);
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		grid.setWidthFull();
		grid.addItemDoubleClickListener(
				event -> new AdditiveDialog(service,dataProvider, event.getItem(), DialogState.EDIT));
		
		GridContextMenu<Additive> contextMenu = grid.addContextMenu();
		contextMenu.addItem("new", event -> 
		new AdditiveDialog(service, dataProvider, new Additive(), DialogState.NEW)
					);
		contextMenu.addItem("delete", event -> {
			event.getItem().ifPresent(item -> {
				service.delete(item.getId());
				dataProvider.refreshAll();
			});
		});
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.setHeightFull();
		this.add(grid);
	}
}
