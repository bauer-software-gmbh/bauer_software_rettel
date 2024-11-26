package de.bauersoft.views.address;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bauersoft.components.GroupByTreeGrid;
import de.bauersoft.data.entities.Address;
import de.bauersoft.data.providers.experimental.AddressGroupByDataProvider;
import de.bauersoft.services.AddressService;

import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("address2")
@Route(value = "address2", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AddressView2 extends VerticalLayout {
	GroupByTreeGrid<Address> grid = new GroupByTreeGrid<Address>(Address.class);

	public AddressView2(AddressService service, AddressGroupByDataProvider dataProvider) {
		
		grid.addColumn("street");
		grid.addColumn("houseNumber");
		grid.addColumn("postalCode");
		grid.addColumn("city");
		grid.setSizeFull();
		grid.setDataProvider(dataProvider);
		/*
		grid.addItemDoubleClickListener(
				event -> new AddressDialog(service, dataProvider, event.getItem(), DialogState.EDIT));
		GridContextMenu<Address> contextMenu = grid.addContextMenu();
		contextMenu.addItem("new institution",
				event -> new AddressDialog(service, dataProvider, new Address(), DialogState.NEW));
		contextMenu.addItem("delete", event -> event.getItem().ifPresent(item -> {
			service.delete(item.getId());
			dataProvider.refreshAll();
		}));
		*/
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.setHeightFull();
		this.add(grid);
		this.setSizeFull();
	}
}
