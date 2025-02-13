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
import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.address.Address;
import de.bauersoft.data.providers.AddressDataProvider;
import de.bauersoft.services.AddressService;
import de.bauersoft.services.InstitutionService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("address")
@Route(value = "address", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AddressView extends Div
{
    AutoFilterGrid<Address> grid = new AutoFilterGrid<Address>(Address.class, false, true);

	private AddressService addressService;
	private AddressDataProvider addressDataProvider;
	private InstitutionService institutionService;

	public AddressView(AddressService addressService, AddressDataProvider addressDataProvider, InstitutionService institutionService)
	{
		this.addressService = addressService;
		this.addressDataProvider = addressDataProvider;
        this.institutionService = institutionService;

        setClassName("content");

		grid.setSizeFull();
		grid.setHeightFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.setDataProvider(addressDataProvider);

        grid.addColumn("street");
        grid.addColumn("number");
        grid.addColumn("postal");
        grid.addColumn("city");

        grid.addItemDoubleClickListener(event ->
		{
			new AddressDialog(addressService, addressDataProvider, event.getItem(), DialogState.EDIT);

		});

        GridContextMenu<Address> contextMenu = grid.addContextMenu();
        contextMenu.addItem("new address", event ->
		{
			new AddressDialog(addressService, addressDataProvider, new Address(), DialogState.NEW);
		});

        GridMenuItem<Address> deleteItem = contextMenu.addItem("delete", event ->
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
					div.add(new Text("Die Adresse \"" + address + "\" kann nicht gelöscht werden da es noch von einigen Institutionen verwendet wird."));

					Notification notification = new Notification(div);
					notification.setDuration(5000);
					notification.setPosition(Notification.Position.MIDDLE);
					notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
					notification.open();

					return;
				}

				addressService.deleteById(item.getId());
				addressDataProvider.refreshAll();
			});
		});

		contextMenu.addGridContextMenuOpenedListener(event ->
		{
			deleteItem.setVisible(event.getItem().isPresent());
		});

        this.add(grid);
    }
}
