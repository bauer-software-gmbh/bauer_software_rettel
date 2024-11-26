package de.bauersoft.views.institution;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.Institution;
import de.bauersoft.data.providers.InstitutionDataProvider;
import de.bauersoft.data.repositories.address.AddressRepository;
import de.bauersoft.data.repositories.field.FieldRepository;
import de.bauersoft.data.repositories.field.InstitutionFieldsRepository;
import de.bauersoft.data.repositories.user.UserRepository;
import de.bauersoft.services.AddressService;
import de.bauersoft.services.InstitutionService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("institution")
@Route(value = "institution", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@CssImport(value = "./themes/rettels/components/auto-filter-grid.css", themeFor = "vaadin-grid")
public class InstitutionView extends Div {
	AutoFilterGrid<Institution> grid = new AutoFilterGrid<Institution>(Institution.class, false, true);
	Column<Institution> refresh;

	public InstitutionView(InstitutionService service, AddressService addressService, FieldRepository fieldRepository,InstitutionFieldsRepository institutionFieldsRepository,
			UserRepository userRepository, AddressRepository addressRepository, InstitutionDataProvider dataProvider) {
		setClassName("content");
		grid.addColumn("name").setResizable(true);
		grid.addColumn("description").setResizable(true);
		grid.addColumn(
				item -> item.getAddress() != null
						? item.getAddress().getStreet() + " " + item.getAddress().getHouseNumber() + ", "
								+ item.getAddress().getPostalCode() + " " + item.getAddress().getCity()
						: "")
				.setKey("address").setHeader("Address").setResizable(true)
				.setSortOrderProvider(direction -> Stream.of(new QuerySortOrder("address.street", direction),
						new QuerySortOrder("address.houseNumber", direction)));
		/*grid.addColumn(
				item -> item.getFields().stream().map(field -> field.getName()).collect(Collectors.joining(", ")))
				.setKey("field.name").setHeader("Field");
		*/
		grid.addColumn(item -> item.getUsers().stream().map(user -> user.getName() + " " + user.getSurname())
				.collect(Collectors.joining(", "))).setHeader("User");
		grid.setDataProvider(dataProvider);
		grid.setMultiSort(true, MultiSortPriority.APPEND);
		grid.addItemDoubleClickListener(event -> new InstitutionDialog(service, addressService, fieldRepository,institutionFieldsRepository,
				userRepository, addressRepository, dataProvider, event.getItem(), DialogState.EDIT));
		GridContextMenu<Institution> contextMenu = grid.addContextMenu();
		contextMenu.addItem("new institution", event -> new InstitutionDialog(service, addressService, fieldRepository,institutionFieldsRepository,
				userRepository, addressRepository, dataProvider, new Institution(), DialogState.NEW));
		contextMenu.addItem("delete", event -> event.getItem().ifPresent(item -> {
			service.delete(item.getId());
			dataProvider.refreshAll();
		}));
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.setHeightFull();
		this.add(grid);
	}
}
