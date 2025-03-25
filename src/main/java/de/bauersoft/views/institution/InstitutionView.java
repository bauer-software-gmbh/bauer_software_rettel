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
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.components.autofiltergridOld.AutoFilterGrid;
import de.bauersoft.data.entities.address.Address;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.providers.AddressDataProvider;
import de.bauersoft.data.providers.InstitutionDataProvider;
import de.bauersoft.services.*;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.Join;

import java.util.stream.Collectors;

@PageTitle("Institutionen")
@Route(value = "institution", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "OFFICE", "OFFICE_ADMIN"})
@CssImport(value = "./themes/rettels/components/auto-filter-grid.css", themeFor = "vaadin-grid")
public class InstitutionView extends Div
{
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

	private final FilterDataProvider<Institution, Long> filterDataProvider;
	private final AutofilterGrid<Institution, Long> grid;

	public InstitutionView(InstitutionService institutionService,
						   InstitutionFieldsService institutionFieldsService,
						   AddressService addressService,
						   FieldService fieldService,
						   UserService userService,
						   InstitutionMultiplierService institutionMultiplierService,
						   CourseService courseService,
						   FieldMultiplierService fieldMultiplierService,
						   OrderService orderService,
						   AllergenService allergenService,
						   InstitutionAllergenService institutionAllergenService,
						   PatternService patternService,
						   InstitutionPatternService institutionPatternService,
						   InstitutionClosingTimeService institutionClosingTimeService)
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

        setClassName("content");

		filterDataProvider = new FilterDataProvider<>(institutionService);
		grid = new AutofilterGrid<>(filterDataProvider);

		grid.setHeightFull();
		grid.setWidthFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.addColumn("name", "Name", Institution::getName, false);
		grid.addColumn("description", "Beschreibung", Institution::getDescription, false);
		grid.addColumn("customerId", "Kunden-Nummer", Institution::getCustomerId, false);
		grid.addColumn("address", "Adresse", institution ->
		{
			Address address = institution.getAddress();
			if(address == null)
				return "";

			return address.getPostal() + " " + address.getCity() + ", " + address.getStreet() + " " + address.getNumber();

		}, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
		{
			Join<Institution, Address> addressJoin = root.join("address");
			return criteriaBuilder.or(
					criteriaBuilder.like(criteriaBuilder.lower(addressJoin.get("postal")), filterInput.toLowerCase() + "%"),
					criteriaBuilder.like(criteriaBuilder.lower(addressJoin.get("city")), filterInput.toLowerCase() + "%"),
					criteriaBuilder.like(criteriaBuilder.lower(addressJoin.get("street")), filterInput.toLowerCase() + "%"),
					criteriaBuilder.like(criteriaBuilder.lower(addressJoin.get("number")), filterInput.toLowerCase() + "%")
			);
		}).enableSorting(false);

		grid.addColumn("users", "Benutzer", institution ->
		{
			return institution.getUsers().stream().map(user -> user.getName() + " " + user.getSurname()).collect(Collectors.joining(", "));

		}, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
		{
			Join<Institution, User> userJoin = root.join("users");
			return criteriaBuilder.or(
					criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("name")), filterInput.toLowerCase() + "%"),
					criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("surname")), filterInput.toLowerCase() + "%")
			);
		}).enableSorting(false);

        grid.addItemDoubleClickListener(event ->
		{
			new InstitutionDialog(filterDataProvider, institutionService, institutionFieldsService, addressService, fieldService, userService, institutionMultiplierService, courseService, fieldMultiplierService, allergenService, institutionAllergenService, patternService, institutionPatternService, institutionClosingTimeService, event.getItem(), DialogState.EDIT);
		});

        GridContextMenu<Institution> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Neue Institution", event ->
		{
			new InstitutionDialog(filterDataProvider, institutionService, institutionFieldsService, addressService, fieldService, userService, institutionMultiplierService, courseService, fieldMultiplierService, allergenService, institutionAllergenService, patternService, institutionPatternService, institutionClosingTimeService, new Institution(), DialogState.NEW);
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
				institutionService.deleteById(item.getId());
				filterDataProvider.refreshAll();
			});
		});

		contextMenu.addGridContextMenuOpenedListener(event ->
		{
			deleteItem.setVisible(event.getItem().isPresent());
		});

        this.add(grid);
    }
}
