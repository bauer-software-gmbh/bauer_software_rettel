package de.bauersoft.views.allergen;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.Allergen;
import de.bauersoft.data.providers.AllergenDataProvider;
import de.bauersoft.services.AllergenService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Allergen")
@Route(value = "allergen", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class AllergenView extends Div {
	
	
	private final AutoFilterGrid<Allergen> grid = new AutoFilterGrid<>(Allergen.class, false, true);

	public AllergenView(AllergenService service, AllergenDataProvider dataProvider) {
		setClassName("content");
		grid.addColumn("name");
		grid.addColumn("description");
		grid.setItems(dataProvider);
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		grid.setWidthFull();
		grid.addItemDoubleClickListener(
				event -> new AllergenDialog(service,dataProvider, event.getItem(), DialogState.EDIT));
		grid.setHeightFull();
		GridContextMenu<Allergen> contextMenu = grid.addContextMenu();
		contextMenu.addItem("new",
				event -> new AllergenDialog(service, dataProvider, new Allergen(), DialogState.NEW));
		contextMenu.addItem("delete", event -> {
			event.getItem().ifPresent(item -> {
				service.delete(item.getId());
				dataProvider.refreshAll();
			});
		});
		this.add(grid);
		
	}

	
}
