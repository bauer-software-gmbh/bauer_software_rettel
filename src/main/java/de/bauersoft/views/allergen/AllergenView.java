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
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.components.autofiltergridOld.AutoFilterGrid;
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
@RolesAllowed({"ADMIN", "KITCHEN_ADMIN"})
@Uses(Icon.class)
public class AllergenView extends Div
{
	private final AllergenService allergenService;
	private final IngredientService ingredientService;
	private final OrderAllergenService orderAllergenService;

	private final FilterDataProvider<Allergen, Long> filterDataProvider;

	private final AutofilterGrid<Allergen, Long> grid;

	public AllergenView(AllergenService allergenService, IngredientService ingredientService, OrderAllergenService orderAllergenService)
	{
        this.allergenService = allergenService;
        this.ingredientService = ingredientService;
        this.orderAllergenService = orderAllergenService;

        setClassName("content");

		filterDataProvider = new FilterDataProvider<>(allergenService);
		grid = new AutofilterGrid<>(filterDataProvider);

		grid.setWidthFull();
		grid.setHeightFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.addColumn("name", "Name", Allergen::getName, false);
		grid.addColumn("description", "Beschreibung", Allergen::getDescription, false);

		grid.AutofilterGridContextMenu()
						.enableGridContextMenu()
						.enableAddItem("Neues Allergen", event ->
						{
							new AllergenDialog(filterDataProvider, allergenService, new Allergen(), DialogState.EDIT);

						}).enableDeleteItem("Löschen", event ->
						{
							event.getItem().ifPresent(item ->
							{
								boolean cancel = false;
								if(ingredientService.getRepository().existsByAllergensId(item.getId()))
								{
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

//								if(orderAllergenService.getRepository().existsByAllergenId(item.getId()))
//								{
//									//TODO später durch fancy dialog ersetzen
//									Div div = new Div();
//									div.setMaxWidth("33vw");
//									div.getStyle().set("white-space", "normal");
//									div.getStyle().set("word-wrap", "break-word");
//
//									div.add(new Text("Das Allergen \"" + item.getName() + "\" kann nicht gelöscht werden, da es noch in einigen Bestellungen verwendet wird."));
//
//									Notification notification = new Notification(div);
//									notification.setDuration(5000);
//									notification.setPosition(Notification.Position.MIDDLE);
//									notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
//									notification.open();
//
//									cancel = true;
//								}

								if(cancel) return;

								allergenService.deleteById(item.getId());
								filterDataProvider.refreshAll();
							});
						});

		grid.addItemDoubleClickListener(event ->
		{
			new AllergenDialog(filterDataProvider, allergenService, event.getItem(), DialogState.EDIT);
		});

		this.add(grid);
	}
}
