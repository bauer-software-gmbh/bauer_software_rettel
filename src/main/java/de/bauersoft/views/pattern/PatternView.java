package de.bauersoft.views.pattern;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.Pattern;
import de.bauersoft.data.providers.PatternDataProvider;
import de.bauersoft.services.PatternService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("pattern")
@Route(value = "pattern", layout = MainLayout.class)
@RolesAllowed(value = { "ADMIN", "ACCOUNTENT" })
public class PatternView extends Div {
	AutoFilterGrid<Pattern> grid = new AutoFilterGrid<Pattern>(Pattern.class, false, true);

	public PatternView(PatternService service, PatternDataProvider dataProvider) {
		setClassName("content");
		grid.addColumn("name");
		grid.addItemDoubleClickListener(
				event -> new PatternDialog(service, dataProvider, event.getItem(), DialogState.EDIT));
		grid.setDataProvider(dataProvider);
		GridContextMenu<Pattern> contextMenu = grid.addContextMenu();
		contextMenu.addItem("new", event -> new PatternDialog(service, dataProvider, new Pattern(), DialogState.NEW));
		contextMenu.addItem("delete", event -> event.getItem().ifPresent(item -> {
			service.delete(item.getId());
			dataProvider.refreshAll();
		}));
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.setHeightFull();
		this.add(grid);
	}
}
