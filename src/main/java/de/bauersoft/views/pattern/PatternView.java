package de.bauersoft.views.pattern;

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
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.providers.PatternDataProvider;
import de.bauersoft.services.PatternService;
import de.bauersoft.services.RecipeService;
import de.bauersoft.services.VariantService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("pattern")
@Route(value = "pattern", layout = MainLayout.class)
@RolesAllowed(value = {"ADMIN", "ACCOUNTENT"})
public class PatternView extends Div
{
    AutoFilterGrid<Pattern> grid = new AutoFilterGrid<Pattern>(Pattern.class, false, true);

    public PatternView(PatternService service,
					   PatternDataProvider dataProvider,
					   VariantService variantService,
					   RecipeService recipeService)
    {
        setClassName("content");

		grid.setHeightFull();
		grid.setWidthFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.setDataProvider(dataProvider);

        grid.addColumn("name");

        grid.addItemDoubleClickListener(event ->
		{
			new PatternDialog(service, dataProvider, event.getItem(), DialogState.EDIT);
		});

        GridContextMenu<Pattern> contextMenu = grid.addContextMenu();
        contextMenu.addItem("new", event ->
		{
			new PatternDialog(service, dataProvider, new Pattern(), DialogState.NEW);
		});

        GridMenuItem<Pattern> deleteItem = contextMenu.addItem("delete", event ->
		{
			event.getItem().ifPresent(item ->
			{
				boolean cancel = false;

				if(variantService.getRepository().existsByPatternId(item.getId()))
				{
					//TODO später durch fancy dialog ersetzen
					Div div = new Div();
					div.setMaxWidth("33vw");
					div.getStyle().set("white-space", "normal");
					div.getStyle().set("word-wrap", "break-word");

					div.add(new Text("Das Pattern \"" + item.getName() + "\" kann nicht gelöscht werden da es noch in einigen Menü-Varianten verwendet wird."));

					Notification notification = new Notification(div);
					notification.setDuration(5000);
					notification.setPosition(Notification.Position.MIDDLE);
					notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
					notification.open();

					cancel = true;
				}

				if(recipeService.getRepository().existsByPatternsId(item.getId()))
				{
					//TODO später durch fancy dialog ersetzen
					Div div = new Div();
					div.setMaxWidth("33vw");
					div.getStyle().set("white-space", "normal");
					div.getStyle().set("word-wrap", "break-word");

					div.add(new Text("Das Pattern \"" + item.getName() + "\" kann nicht gelöscht werden da es noch in einigen Rezepten verwendet wird."));

					Notification notification = new Notification(div);
					notification.setDuration(5000);
					notification.setPosition(Notification.Position.MIDDLE);
					notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
					notification.open();

					cancel = true;
				}

				if(cancel) return;

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
