package de.bauersoft.views.institution;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.address.Address;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.providers.AddressDataProvider;
import de.bauersoft.data.providers.InstitutionDataProvider;
import de.bauersoft.data.repositories.address.AddressRepository;
import de.bauersoft.data.repositories.field.FieldRepository;
import de.bauersoft.data.repositories.institutionfields.InstitutionFieldsRepository;
import de.bauersoft.data.repositories.user.UserRepository;
import de.bauersoft.services.*;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@PageTitle("institution")
@Route(value = "institution", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@CssImport(value = "./themes/rettels/components/auto-filter-grid.css", themeFor = "vaadin-grid")
public class InstitutionView extends Div
{
    private AutoFilterGrid<Institution> grid = new AutoFilterGrid<Institution>(Institution.class, false, true);

	private InstitutionService institutionService;
	private InstitutionFieldsService institutionFieldsService;
	private AddressService addressService;
	private FieldService fieldService;
	private UserService userService;
	private InstitutionMultiplierService institutionMultiplierService;
	private CourseService courseService;
	private FieldMultiplierService fieldMultiplierService;

	private InstitutionDataProvider institutionDataProvider;
	private AddressDataProvider addressDataProvider;

	public InstitutionView(InstitutionService institutionService, InstitutionFieldsService institutionFieldsService, AddressService addressService, FieldService fieldService, UserService userService, InstitutionMultiplierService institutionMultiplierService, CourseService courseService, FieldMultiplierService fieldMultiplierService, InstitutionDataProvider institutionDataProvider, AddressDataProvider addressDataProvider)
	{
		this.institutionService = institutionService;
		this.institutionFieldsService = institutionFieldsService;
		this.addressService = addressService;
		this.fieldService = fieldService;
		this.userService = userService;
        this.institutionMultiplierService = institutionMultiplierService;
        this.courseService = courseService;
        this.fieldMultiplierService = fieldMultiplierService;
        this.institutionDataProvider = institutionDataProvider;
        this.addressDataProvider = addressDataProvider;

        setClassName("content");

		grid.setHeightFull();
		grid.setWidthFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.setDataProvider(institutionDataProvider);

        grid.addColumn("name")
				.setResizable(true);

        grid.addColumn("description")
						.setResizable(true);

        grid.addColumn(item -> item.getAddress() != null
                                ? item.getAddress().getStreet() + " " + item.getAddress().getNumber() + ", "
                                + item.getAddress().getPostal() + " " + item.getAddress().getCity()
                                : "")
				.setHeader("Adresse")
				.setResizable(true);
		//TODO sorting geht nicht warum auch immer ._.

//                .setKey("address").setHeader("Address").setResizable(true)
//                .setSortOrderProvider(direction -> Stream.of(new QuerySortOrder("address.street", direction),
//                        new QuerySortOrder("address.number", direction)));


//		grid.addColumn(institution ->
//		{
//			Address address = institution.getAddress();
//			return (address == null)
//					? ""
//					: address.getStreet() + " " + address.getNumber() + ", " + address.getPostal() + " " + address.getCity();
//		}).setHeader("Adresse")
//				.setResizable(true)
//				.setKey("address")
//				.setSortOrderProvider(sortDirection ->
//				{
//					return Stream.of(
//							new QuerySortOrder("address.street", sortDirection),
//							new QuerySortOrder("address.number", sortDirection)
//					);
//				});

        grid.addColumn(item ->
		{
			return item.getUsers()
					.stream()
					.map(user -> user.getName() + " " + user.getSurname())
					.collect(Collectors.joining(", "));
		}).setHeader("User");

        grid.setMultiSort(true, MultiSortPriority.APPEND);
        grid.addItemDoubleClickListener(event ->
		{
			new InstitutionDialog(institutionService, institutionFieldsService, addressService, fieldService, userService, institutionMultiplierService, courseService, fieldMultiplierService, institutionDataProvider, addressDataProvider, event.getItem(), DialogState.EDIT);
		});

        GridContextMenu<Institution> contextMenu = grid.addContextMenu();
        contextMenu.addItem("new institution", event ->
		{
			new InstitutionDialog(institutionService, institutionFieldsService, addressService, fieldService, userService, institutionMultiplierService, courseService, fieldMultiplierService, institutionDataProvider, addressDataProvider, new Institution(), DialogState.NEW);
		});

        GridMenuItem<Institution> deleteItem = contextMenu.addItem("delete", event ->
		{
			event.getItem().ifPresent(item ->
			{
				institutionMultiplierService.getRepository().deleteAllByInstitution(item);
				institutionFieldsService.getRepository().deleteAllByInstitutionId(item.getId());
				institutionService.delete(item.getId());
				institutionDataProvider.refreshAll();
			});
		});

		contextMenu.addGridContextMenuOpenedListener(event ->
		{
			deleteItem.setVisible(event.getItem().isPresent());
		});

        this.add(grid);
    }
}
