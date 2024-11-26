package de.bauersoft.views.field;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.Field;
import de.bauersoft.data.providers.FieldDataProvider;
import de.bauersoft.services.FieldService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("field")
@Route(value = "field", layout = MainLayout.class)
@RolesAllowed(value = { "ADMIN", "ACCOUNTENT" })
public class FieldView extends Div {
	
	AutoFilterGrid<Field> grid = new AutoFilterGrid<Field>(Field.class, false, true);

	public FieldView(FieldService service,  FieldDataProvider dataProvider) {
		setClassName("content");
		grid.addColumn("name");
		grid.addItemDoubleClickListener(event -> new FieldDialog(service,dataProvider, event.getItem(), DialogState.EDIT));
		grid.setDataProvider(dataProvider);
		GridContextMenu<Field> contextMenu = grid.addContextMenu();
		contextMenu.addItem("new", event -> new FieldDialog(service,dataProvider, new Field(), DialogState.NEW));
		contextMenu.addItem("delete", event -> event.getItem().ifPresent(item -> {
			service.delete(item.getId());
			dataProvider.refreshAll();
		}));
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.setHeightFull();
		this.add(grid);
	}
}
