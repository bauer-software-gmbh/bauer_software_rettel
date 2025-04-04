package de.bauersoft.views.additive;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.providers.AdditiveDataProvider;
import de.bauersoft.services.AdditiveService;
import de.bauersoft.services.IngredientService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Zusatzstoffe")
@Route(value = "additive", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "KITCHEN_ADMIN"})
@Uses(Icon.class)
public class AdditiveView extends Div
{
	private final AdditiveService additiveService;
	private final IngredientService ingredientService;

	private final AdditiveDataProvider additiveDataProvider;

	private final AutofilterGrid<Additive, Long> grid;

	public AdditiveView(AdditiveService additiveService,
                        IngredientService ingredientService,
						AdditiveDataProvider additiveDataProvider)
	{
        this.additiveService = additiveService;
        this.ingredientService = ingredientService;
        this.additiveDataProvider = additiveDataProvider;

        setClassName("content");

		grid = new AutofilterGrid<>(additiveDataProvider);

		grid.setHeightFull();
		grid.setWidthFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.addColumn("name", "Name", Additive::getName, false);
		grid.addColumn("description", "Beschreibung", Additive::getDescription, false);

		grid.addItemDoubleClickListener(event ->
		{
			new AdditiveDialog(additiveService, additiveDataProvider, event.getItem(), DialogState.EDIT);
		});
		
		GridContextMenu<Additive> contextMenu = grid.addContextMenu();
		contextMenu.addItem("Neuer Zusatzstoff", event ->
		{
			new AdditiveDialog(additiveService, additiveDataProvider, new Additive(), DialogState.NEW);
		});

		GridMenuItem<Additive> deleteItem = contextMenu.addItem("Löschen", event ->
		{
			event.getItem().ifPresent(item ->
			{
				if(ingredientService.getRepository().existsByAdditivesId(item.getId()))
				{
					Div div = new Div();
					div.setMaxWidth("33vw");
					div.getStyle().set("white-space", "normal");
					div.getStyle().set("word-wrap", "break-word");

					div.add(new Text("Der Zusatzstoff \"" + item.getName() + "\" kann nicht gelöscht werden, da er noch bei einigen Zutaten hinterlegt ist."));

					Notification notification = new Notification(div);
					notification.setDuration(5000);
					notification.setPosition(Notification.Position.MIDDLE);
					notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
					notification.open();
					return;
				}

				additiveService.deleteById(item.getId());
				additiveDataProvider.refreshAll();
			});
		});

		contextMenu.addGridContextMenuOpenedListener(event ->
		{
			deleteItem.setVisible(event.getItem().isPresent());
		});
		this.add(grid);
	}
}
