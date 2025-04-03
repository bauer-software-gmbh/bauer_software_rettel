//package de.bauersoft.views.tour;
//
//import com.vaadin.flow.component.Component;
//import com.vaadin.flow.component.grid.Grid;
//import com.vaadin.flow.component.grid.GridVariant;
//import com.vaadin.flow.component.html.Div;
//import com.vaadin.flow.component.orderedlayout.FlexComponent;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.textfield.TextField;
//import com.vaadin.flow.data.renderer.ComponentRenderer;
//import com.vaadin.flow.data.renderer.LitRenderer;
//import com.vaadin.flow.data.renderer.Renderer;
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//import de.bauersoft.components.autofilter.FilterDataProvider;
//import de.bauersoft.components.autofilter.grid.AutofilterGrid;
//import de.bauersoft.data.entities.institution.Institution;
//import de.bauersoft.data.entities.tour.driver.Driver;
//import de.bauersoft.data.entities.tour.tour.Tour;
//import de.bauersoft.data.entities.tour.tour.TourInstitution;
//import de.bauersoft.data.entities.user.User;
//import de.bauersoft.services.InstitutionService;
//import de.bauersoft.services.tour.*;
//import de.bauersoft.views.DialogState;
//import de.bauersoft.views.MainLayout;
//import jakarta.annotation.security.RolesAllowed;
//
//import java.util.stream.Collectors;
//
//@PageTitle("Tourenerstellung")
//@Route(value = "createtour", layout = MainLayout.class)
//@RolesAllowed("ADMIN")
//public class TourView extends Div
//{
//    private final TourService tourService;
//    private final DriverService driverService;
//    private final VehicleDowntimeService vehicleDowntimeService;
//    private final VehicleService vehicleService;
//    private final InstitutionService institutionService;
//    private final TourInstitutionService tourInstitutionService;
//
//    private final FilterDataProvider<Tour, Long> filterDataProvider;
//    private final AutofilterGrid<Tour, Long> grid;
//
//    public TourView(TourService tourService, DriverService driverService, VehicleDowntimeService vehicleDowntimeService, VehicleService vehicleService, InstitutionService institutionService, TourInstitutionService tourInstitutionService)
//    {
//        this.tourService = tourService;
//        this.driverService = driverService;
//        this.vehicleDowntimeService = vehicleDowntimeService;
//        this.vehicleService = vehicleService;
//        this.institutionService = institutionService;
//        this.tourInstitutionService = tourInstitutionService;
//
//        setClassName("content");
//
//        filterDataProvider = new FilterDataProvider<>(tourService);
//        grid = new AutofilterGrid<>(filterDataProvider);
//
//        grid.setHeightFull();
//        grid.setWidthFull();
//        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
////
////        grid.addColumn(createToggleInstitutionDetailsRenderer(grid))
////                        .setWidth("80px").setFlexGrow(0).setFrozen(true).setFrozen(true);
////
////        grid.setDetailsVisibleOnClick(false);
//
//        grid.addColumn("name", "Name", Tour::getName, false);
//        grid.addColumn("vehicle", "Fahrzeug", tour ->
//        {
//            return tour.getVehicle().getLicensePlate();
//        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
//        {
//            return criteriaBuilder.like(criteriaBuilder.lower(path.get("licensePlate")), "%" + filterInput.toLowerCase() + "%");
//        });
//        grid.addColumn("requiredDrivers", "Benötigte Fahrer", tour -> String.valueOf(tour.getRequiredDrivers()), false);
//        grid.addColumn("driver", "Fahrer", tour ->
//        {
//            User user = tour.getDriver().getUser();
//            return user.getName() + " " + user.getSurname();
//
//        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
//        {
//            return criteriaBuilder.or(
//                    criteriaBuilder.like(criteriaBuilder.lower(path.get("user").get("name")), filterInput.toLowerCase() + "%"),
//                    criteriaBuilder.like(criteriaBuilder.lower(path.get("user").get("surname")), filterInput.toLowerCase() + "%")
//            );
//        });
//
//        grid.addColumn("coDriver", "Beifahrer", tour ->
//        {
//            Driver coDriver = tour.getCoDriver();
//            if(coDriver == null) return "";
//
//            User user = coDriver.getUser();
//            return (user == null) ? "" : user.getName() + " " + user.getSurname();
//
//        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
//        {
//            return criteriaBuilder.or(
//                    criteriaBuilder.like(criteriaBuilder.lower(path.get("user").get("name")), filterInput.toLowerCase() + "%"),
//                    criteriaBuilder.like(criteriaBuilder.lower(path.get("user").get("surname")), filterInput.toLowerCase() + "%")
//            );
//        });
//
//        grid.addColumn("institutions", "Institutionen", tour ->
//        {
//            return tour.getInstitutions().stream().map(TourInstitution::getInstitution).map(Institution::getName).collect(Collectors.joining(", "));
//
//        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
//        {
//            return criteriaBuilder.like(criteriaBuilder.lower(path.get("institution").get("name")), filterInput.toLowerCase() + "%");
//        }).enableSorting(false).getGridColumn().setKey("institutions");
////
////        grid.addColumnResizeListener(event ->
////        {
////            if(event.getResizedColumn().getKey() != "institutions") return;
////
////            grid.setItemDetailsRenderer(createDetailsRenderer(event.getResizedColumn().getWidth()));
////        });
//
//
//        grid.AutofilterGridContextMenu().enableGridContextMenu()
//                        .enableAddItem("Neue Tour", event ->
//                        {
//                            new TourDialog(filterDataProvider, tourService, driverService, vehicleService, vehicleDowntimeService, institutionService, tourInstitutionService, new Tour(), DialogState.NEW);
//                        }).enableDeleteItem("Löschen", event ->
//                        {
//
//                        });
//
//        grid.addItemDoubleClickListener(event ->
//        {
//            new TourDialog(filterDataProvider, tourService, driverService, vehicleService, vehicleDowntimeService, institutionService, tourInstitutionService, event.getItem(), DialogState.EDIT);
//        });
//
//        this.add(grid);
//
//        //grid.setItemDetailsRenderer(createDetailsRenderer(grid.getColumnByKey("institutions").getWidth()));
//    }
//
//    private ComponentRenderer<Component, Tour> createDetailsRenderer(String width)
//    {
//        return new ComponentRenderer<Component, Tour>(tour ->
//        {
//            VerticalLayout layout = new VerticalLayout();
//            layout.setWidthFull();
//            layout.setPadding(false);
//            layout.setAlignItems(FlexComponent.Alignment.END);
//
//            for(TourInstitution tourInstitution : tour.getInstitutions())
//            {
//                TextField institutionField = new TextField();
//                institutionField.setReadOnly(true);
//                institutionField.setWidth(width);
//                institutionField.setValue(tourInstitution.getInstitution().getName());
//                layout.add(institutionField);
//            }
//
//            return layout;
//        });
//    }
//
//    private Renderer<Tour> createToggleInstitutionDetailsRenderer(Grid<Tour> grid)
//    {
//        return LitRenderer
//                .<Tour> of("""
//                <vaadin-button
//                    theme="tertiary icon"
//                    aria-label="Toggle details"
//                    aria-expanded="${model.detailsOpened ? 'true' : 'false'}"
//                    @click="${handleClick}"
//                >
//                    <vaadin-icon
//                    .icon="${model.detailsOpened ? 'lumo:angle-down' : 'lumo:angle-right'}"
//                    ></vaadin-icon>
//                </vaadin-button>
//            """)
//                .withFunction("handleClick",
//                        tour -> grid.setDetailsVisible(tour,
//                                !grid.isDetailsVisible(tour)));
//    }
//}
