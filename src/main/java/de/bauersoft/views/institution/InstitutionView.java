package de.bauersoft.views.institution;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.providers.AddressDataProvider;
import de.bauersoft.data.providers.InstitutionDataProvider;
import de.bauersoft.services.*;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

import java.util.stream.Collectors;

@PageTitle("Institutionen")
@Route(value = "institution", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "OFFICE", "OFFICE_ADMIN"})
@CssImport(value = "./themes/rettels/components/auto-filter-grid.css", themeFor = "vaadin-grid")
public class InstitutionView extends Div
{
    private AutoFilterGrid<Institution> grid = new AutoFilterGrid<>(Institution.class, false, true);

	private InstitutionService institutionService;
	private InstitutionFieldsService institutionFieldsService;
	private AddressService addressService;
	private FieldService fieldService;
	private UserService userService;
	private InstitutionMultiplierService institutionMultiplierService;
	private CourseService courseService;
	private FieldMultiplierService fieldMultiplierService;
	private OrderService orderService;
	private AllergenService allergenService;
	private InstitutionAllergenService institutionAllergenService;
	private PatternService patternService;
	private InstitutionPatternService institutionPatternService;
	private InstitutionClosingTimeService institutionClosingTimeService;

	private InstitutionDataProvider institutionDataProvider;
	private AddressDataProvider addressDataProvider;

	public InstitutionView(InstitutionService institutionService, InstitutionFieldsService institutionFieldsService, AddressService addressService, FieldService fieldService, UserService userService, InstitutionMultiplierService institutionMultiplierService, CourseService courseService, FieldMultiplierService fieldMultiplierService, OrderService orderService, AllergenService allergenService, InstitutionAllergenService institutionAllergenService, PatternService patternService, InstitutionPatternService institutionPatternService, InstitutionClosingTimeService institutionClosingTimeService, InstitutionDataProvider institutionDataProvider, AddressDataProvider addressDataProvider)
	{
		this.institutionService = institutionService;
		this.institutionFieldsService = institutionFieldsService;
		this.addressService = addressService;
		this.fieldService = fieldService;
		this.userService = userService;
        this.institutionMultiplierService = institutionMultiplierService;
        this.courseService = courseService;
        this.fieldMultiplierService = fieldMultiplierService;
        this.orderService = orderService;
        this.allergenService = allergenService;
        this.institutionAllergenService = institutionAllergenService;
        this.patternService = patternService;
        this.institutionPatternService = institutionPatternService;
        this.institutionClosingTimeService = institutionClosingTimeService;
        this.institutionDataProvider = institutionDataProvider;
        this.addressDataProvider = addressDataProvider;

        setClassName("content");

		grid.setHeightFull();
		grid.setWidthFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.setDataProvider(institutionDataProvider);

        grid.addColumn("name")
				.setResizable(true).setHeader("Name");

        grid.addColumn("description")
						.setResizable(true).setHeader("Beschreibung");

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
		}).setHeader("Benutzer");

        grid.setMultiSort(true, MultiSortPriority.APPEND);
        grid.addItemDoubleClickListener(event ->
		{
			new InstitutionDialog(institutionService, institutionFieldsService, addressService, fieldService, userService, institutionMultiplierService, courseService, fieldMultiplierService, allergenService, institutionAllergenService, patternService, institutionPatternService, institutionClosingTimeService, institutionDataProvider, addressDataProvider, event.getItem(), DialogState.EDIT);
		});

        GridContextMenu<Institution> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Neue Institution", event ->
		{
			new InstitutionDialog(institutionService, institutionFieldsService, addressService, fieldService, userService, institutionMultiplierService, courseService, fieldMultiplierService, allergenService, institutionAllergenService, patternService, institutionPatternService, institutionClosingTimeService, institutionDataProvider, addressDataProvider, new Institution(), DialogState.NEW);
		});

        GridMenuItem<Institution> deleteItem = contextMenu.addItem("Löschen", event ->
		{
			event.getItem().ifPresent(item ->
			{
				if(orderService.existsByInstitution(item))
				{
					Div div = new Div();
					div.setMaxWidth("33vw");
					div.getStyle().set("white-space", "normal");
					div.getStyle().set("word-wrap", "break-word");

					div.add(new Text("Die Institution " + item.getName() + " kann nicht gelöscht werden da sie von einigen Bestellungen verwendet wird."));

					Notification notification = new Notification(div);
					notification.setDuration(5000);
					notification.setPosition(Notification.Position.MIDDLE);
					notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
					notification.open();
					return;
				}
//				institutionMultiplierService.getRepository().deleteAllByInstitutionId(item.getId());
//				institutionFieldsService.getRepository().deleteAllByInstitutionId(item.getId());
				institutionService.deleteById(item.getId());
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
