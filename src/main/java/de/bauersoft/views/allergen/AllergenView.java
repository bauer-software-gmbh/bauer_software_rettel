package de.bauersoft.views.allergen;

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
import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.providers.AllergenDataProvider;
import de.bauersoft.services.AllergenService;
import de.bauersoft.services.IngredientService;
import de.bauersoft.services.OrderAllergenService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Allergene")
@Route(value = "allergen", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class AllergenView extends Div
{
	private final AutoFilterGrid<Allergen> grid = new AutoFilterGrid<>(Allergen.class, false, true);

	public AllergenView(AllergenService service,
						AllergenDataProvider dataProvider,
						IngredientService ingredientService,
						OrderAllergenService orderAllergenService)

	{
		setClassName("content");

		grid.setWidthFull();
		grid.setHeightFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.setItems(dataProvider);

		grid.addColumn("name").setHeader("Name");
		grid.addColumn("description").setHeader("Beschreibung");

		grid.addItemDoubleClickListener(event ->
                new AllergenDialog(service,dataProvider, event.getItem(), DialogState.EDIT));

		GridContextMenu<Allergen> contextMenu = grid.addContextMenu();
		contextMenu.addItem("Neues Allergen", event ->
			new AllergenDialog(service, dataProvider, new Allergen(), DialogState.NEW));

		GridMenuItem<Allergen> deleteItem = contextMenu.addItem("Löschen", event ->
		{
			event.getItem().ifPresent(item ->
			{
				boolean cancel = false;

				//List<Ingredient> ingredients = ingredientService.getRepository().findAllByAllergensId(item.getId());
				if(ingredientService.getRepository().existsByAllergensId(item.getId()))
				{
					//TODO später durch fancy dialog ersetzen
					Div div = new Div();
					div.setMaxWidth("33vw");
					div.getStyle().set("white-space", "normal");
					div.getStyle().set("word-wrap", "break-word");

					div.add(new Text("Das Allergen \"" + item.getName() + "\" kann nicht gelöscht werden, da es noch in einigen Zutaten verwendet wird."));

					Notification notification = new Notification(div);
					notification.setDuration(5000);
					notification.setPosition(Notification.Position.MIDDLE);
					notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
					notification.open();

					cancel = true;
				}

				if(orderAllergenService.getRepository().existsByAllergenId(item.getId()))
				{
					//TODO später durch fancy dialog ersetzen
					Div div = new Div();
					div.setMaxWidth("33vw");
					div.getStyle().set("white-space", "normal");
					div.getStyle().set("word-wrap", "break-word");

					div.add(new Text("Das Allergen \"" + item.getName() + "\" kann nicht gelöscht werden, da es noch in einigen Bestellungen verwendet wird."));

					Notification notification = new Notification(div);
					notification.setDuration(5000);
					notification.setPosition(Notification.Position.MIDDLE);
					notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
					notification.open();

					cancel = true;
				}

				if(cancel) return;

				service.deleteById(item.getId());
				dataProvider.refreshAll();
			});
		});

		contextMenu.addGridContextMenuOpenedListener(event ->
			deleteItem.setVisible(event.getItem().isPresent()));

		this.add(grid);
	}
}
