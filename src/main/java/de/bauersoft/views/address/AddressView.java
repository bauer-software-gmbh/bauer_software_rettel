package de.bauersoft.views.address;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.data.entities.address.Address;
import de.bauersoft.services.AddressService;
import de.bauersoft.services.InstitutionService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Adressen")
@Route(value = "address", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "OFFICE", "OFFICE_ADMIN"})
public class AddressView extends Div
{
	private final AddressService addressService;
	private final InstitutionService institutionService;

    private final FilterDataProvider<Address, Long> filterDataProvider;
	private final AutofilterGrid<Address, Long> grid;

	public AddressView(AddressService addressService, InstitutionService institutionService)
	{
		this.addressService = addressService;
        this.institutionService = institutionService;

        setClassName("content");

		filterDataProvider = new FilterDataProvider<>(addressService);
		grid = new AutofilterGrid<>(filterDataProvider);

		grid.setSizeFull();
		grid.setHeightFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.addColumn("street", "Straße", Address::getStreet, false);
		grid.addColumn("number", "Hausnummer", Address::getNumber, false);
		grid.addColumn("postal", "PLZ", Address::getPostal, false);
		grid.addColumn("city", "Ort", Address::getCity, false);

		grid.AutofilterGridContextMenu()
						.enableGridContextMenu()
						.enableAddItem("Neue Adresse", event ->
						{
							new AddressDialog(filterDataProvider, addressService, new Address(), DialogState.NEW);

						}).enableDeleteItem("Löschen", event ->
						{
							event.getItem().ifPresent(item ->
							{
								if(institutionService.getRepository().existsInstitutionsByAddressId(item.getId()))
								{
									Div div = new Div();
									div.setMaxWidth("33vw");
									div.getStyle().set("white-space", "normal");
									div.getStyle().set("word-wrap", "break-word");

									String address = item.getStreet() + " " + item.getNumber() + ", " + item.getPostal() + " " + item.getCity();
									div.add(new Text("Die Adresse \"" + address + "\" kann nicht gelöscht werden, da sie noch von anderen Institutionen verwendet wird."));

									Notification notification = new Notification(div);
									notification.setDuration(5000);
									notification.setPosition(Notification.Position.MIDDLE);
									notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
									notification.open();

									return;
								}

								addressService.deleteById(item.getId());
								filterDataProvider.refreshAll();
							});
						});

		grid.addItemDoubleClickListener(event ->
		{
			new AddressDialog(filterDataProvider, addressService, event.getItem(), DialogState.EDIT);
		});

        this.add(grid);
    }
}
