package de.bauersoft.views.tourCreation;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.DateTimeUtils;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.data.entities.tour.driver.Driver;
import de.bauersoft.data.entities.tour.tour.Tour;
import de.bauersoft.data.entities.tour.tour.TourInstitution;
import de.bauersoft.data.entities.tour.vehicle.VehicleDowntime;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.services.InstitutionService;
import de.bauersoft.services.tour.*;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.Join;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@PageTitle("Tourenplanung")
@Route(value = "tour", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@CssImport(
        themeFor = "vaadin-grid",
        value = "./themes/rettels/views/tourCreation.css")
public class TourView extends Div
{
    private final TourService tourService;
    private final DriverService driverService;
    private final VehicleDowntimeService vehicleDowntimeService;
    private final VehicleService vehicleService;
    private final InstitutionService institutionService;
    private final TourInstitutionService tourInstitutionService;

    private final FilterDataProvider<Tour, Long> filterDataProvider;
    private final AutofilterGrid<Tour, Long> grid;

    private final AtomicBoolean details;

    public TourView(TourService tourService, DriverService driverService, VehicleDowntimeService vehicleDowntimeService, VehicleService vehicleService, InstitutionService institutionService, TourInstitutionService tourInstitutionService)
    {
        this.tourService = tourService;
        this.driverService = driverService;
        this.vehicleDowntimeService = vehicleDowntimeService;
        this.vehicleService = vehicleService;
        this.institutionService = institutionService;
        this.tourInstitutionService = tourInstitutionService;

        setClassName("content");

        filterDataProvider = new FilterDataProvider<>(tourService);
        grid = new AutofilterGrid<>(filterDataProvider);

        details = new AtomicBoolean(false);

        grid.setHeightFull();
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
//
//        grid.addColumn(createToggleInstitutionDetailsRenderer(grid))
//                        .setWidth("80px").setFlexGrow(0).setFrozen(true);

//        grid.setDetailsVisibleOnClick(false);

        grid.setClassNameGenerator(tour ->
        {
            Optional<VehicleDowntime> nextDTOptional = vehicleDowntimeService.getNextVehicleDowntime(tour.getVehicle().getId());
            if(nextDTOptional.isPresent())
            {
                VehicleDowntime nextDT = nextDTOptional.get();
                if(isVehicleOff(LocalDate.now(), nextDT.getStartDate(), nextDT.getEndDate()))
                    return "attention-row";
            }

            if(tour.getDriver() == null)
                return "attention-row";


            if(tour.getDrivesUntil() != null)
            {
                if(LocalDate.now().isAfter(tour.getDrivesUntil()))
                    return "attention-row";
            }

            if(tour.getCoDrivesUntil() != null)
            {
                if(LocalDate.now().isAfter(tour.getCoDrivesUntil()))
                    return "attention-row";
            }

            return null;
        });


        grid.addColumn(createToggleInstitutionDetailsRenderer(grid))
                .setKey("details").setWidth("50px").setFlexGrow(0).setFrozen(true);

        grid.setDetailsVisibleOnClick(false);
        grid.setItemDetailsRenderer(createDetailsRenderer());


        grid.addRendererColumn("name", "Name", new ComponentRenderer<>(tour ->
        {
            if(tour.isHolidayMode())
            {
                return new HorizontalLayout(new Text(tour.getName()), LineAwesomeIcon.COCKTAIL_SOLID.create());
            }else return new Text(tour.getName());

        }), false);


        grid.addRendererColumn("vehicle", "Fahrzeug", new ComponentRenderer<>(tour ->
        {
            Div cell = new Div();
            cell.add(new Text(tour.getVehicle().getLicensePlate()));
            cell.getStyle().set("align-content", "center");

            Optional<VehicleDowntime> nextDTOptional = vehicleDowntimeService.getNextVehicleDowntime(tour.getVehicle().getId());
            if(nextDTOptional.isPresent())
            {
                VehicleDowntime nextDT = nextDTOptional.get();
                if(isVehicleOff(LocalDate.now(), nextDT.getStartDate(), nextDT.getEndDate()))
                {
                    HorizontalLayout vehicleOffLayout = new HorizontalLayout();
                    vehicleOffLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
                    vehicleOffLayout.add(LineAwesomeIcon.EXCLAMATION_TRIANGLE_SOLID.create(), cell);

                    return vehicleOffLayout;
                }
            }

            return cell;
        }), (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.like(criteriaBuilder.lower(path.get("licensePlate")), "%" + filterInput.toLowerCase() + "%");
        });

        //grid.addColumn("requiredDrivers", "Benötigte Fahrer", tour -> String.valueOf(tour.getRequiredDrivers()), false);
        grid.addRendererColumn("driver", "Fahrer", new ComponentRenderer<>(tour ->
        {
            Driver driver = tour.getDriver();
            if(driver == null)
            {
                HorizontalLayout noDriverLayout = new HorizontalLayout();
                noDriverLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
                noDriverLayout.add(LineAwesomeIcon.EXCLAMATION_TRIANGLE_SOLID.create(), new Text("Fahrer fehlt!"));

                return noDriverLayout;
            }

            User user = driver.getUser();

            Div cell = new Div();
            cell.add(new Text(user.getName() + " " + user.getSurname()));
            cell.getStyle().set("align-content", "center");

            if(LocalDate.now().isAfter(tour.getDrivesUntil()))
                return new HorizontalLayout(LineAwesomeIcon.EXCLAMATION_TRIANGLE_SOLID.create(), cell);

            return cell;

        }), (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(path.get("user").get("name")), filterInput.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(path.get("user").get("surname")), filterInput.toLowerCase() + "%")
            );
        });


        grid.addRendererColumn("coDriver", "Beifahrer", new ComponentRenderer<>(tour ->
        {
            if(tour.getCoDriver() == null) return new Div();
            User user = tour.getCoDriver().getUser();

            Div cell = new Div();
            cell.add(new Text(user.getName() + " " + user.getSurname()));
            cell.getStyle().set("align-content", "center");

            if(LocalDate.now().isAfter(tour.getCoDrivesUntil()))
                return new HorizontalLayout(LineAwesomeIcon.EXCLAMATION_TRIANGLE_SOLID.create(), cell);

            return cell;

        }), (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(path.get("user").get("name")), filterInput.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(path.get("user").get("surname")), filterInput.toLowerCase() + "%")
            );
        });


        grid.addColumn("institutions", "Institutionen", tour ->
        {
            return tour.getInstitutions().stream()
                    .sorted(Comparator.comparing(tourInstitution -> tourInstitution.getInstitution().getName()))
                    .map(tourInstitution -> tourInstitution.getInstitution().getName())
                    .collect(Collectors.joining(", "));

        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            Join<Tour, TourInstitution> tourInstitutionJoin = root.join("institutions");
            criteriaQuery.distinct(true);

            return criteriaBuilder.like(criteriaBuilder.lower(tourInstitutionJoin.get("institution").get("name")), filterInput.toLowerCase() + "%");
        }).enableSorting(false).getGridColumn().setKey("institutions");


        grid.AutofilterGridContextMenu().enableGridContextMenu()
                        .enableAddItem("Neue Tour", event ->
                        {
                            new TourDialog(filterDataProvider, tourService, driverService, vehicleService, vehicleDowntimeService, institutionService, tourInstitutionService, new Tour(), DialogState.NEW);
                        }).enableDeleteItem("Löschen", event ->
                        {
                            event.getItem().ifPresent(tour ->
                            {
                                driverService.deleteAllDriveableToursByTourId(tour.getId());
                                tourService.delete(tour);
                                filterDataProvider.refreshAll();
                            });
                        });

        grid.addItemDoubleClickListener(event ->
        {
            new TourDialog(filterDataProvider, tourService, driverService, vehicleService, vehicleDowntimeService, institutionService, tourInstitutionService, event.getItem(), DialogState.EDIT);
        });

        this.add(grid);
    }

    private ComponentRenderer<Component, Tour> createDetailsRenderer()
    {
        return new ComponentRenderer<Component, Tour>(tour ->
        {
            HorizontalLayout layout = new HorizontalLayout();

            HorizontalLayout tourNameLayout = new HorizontalLayout();
            tourNameLayout.setWidth("100%");

            tourNameLayout.add(LineAwesomeIcon.TAG_SOLID.create(), new Text(tour.getName()));

            if(tour.isHolidayMode())
                tourNameLayout.add(LineAwesomeIcon.COCKTAIL_SOLID.create());

            layout.add(tourNameLayout);

            VerticalLayout carLayout = new VerticalLayout();
            carLayout.setPadding(false);
            carLayout.setWidth("100%");

            HorizontalLayout licenseLayout = new HorizontalLayout();
            licenseLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
            licenseLayout.add(LineAwesomeIcon.TRUCK_SOLID.create(), new Text(tour.getVehicle().getLicensePlate()));
            carLayout.add(licenseLayout);

            HorizontalLayout nextDowntimeLayout = new HorizontalLayout();
            nextDowntimeLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
            nextDowntimeLayout.add(LineAwesomeIcon.OIL_CAN_SOLID.create());

            Optional<VehicleDowntime> nextDowntimeOptional = vehicleDowntimeService.getNextVehicleDowntime(tour.getVehicle().getId());
            if(nextDowntimeOptional.isPresent())
            {
                VehicleDowntime nextDowntime = nextDowntimeOptional.get();
                if(nextDowntime.getEndDate() == null)
                {
                    nextDowntimeLayout.add(new Text(DateTimeUtils.DATE_FORMATTER.format(nextDowntimeOptional.get().getStartDate())));
                }else nextDowntimeLayout.add(new Text(DateTimeUtils.DATE_FORMATTER.format(nextDowntimeOptional.get().getStartDate()) + " - " + DateTimeUtils.DATE_FORMATTER.format(nextDowntimeOptional.get().getEndDate())));

            }else nextDowntimeLayout.add(new Text("/"));


            carLayout.add(licenseLayout, nextDowntimeLayout);
            layout.add(carLayout);

            VerticalLayout driverLayout = new VerticalLayout();
            driverLayout.setPadding(false);
            driverLayout.setWidth("100%");

            HorizontalLayout driverNameLayout = new HorizontalLayout();
            driverNameLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
            driverNameLayout.add(LineAwesomeIcon.CAR_SOLID.create());

            Driver driver = tour.getDriver();
            if(driver == null)
            {
                driverNameLayout.add(new Text("/"));
            }else driverNameLayout.add(new Text(tour.getDriver().getUser().getName() + " " + tour.getDriver().getUser().getSurname()));

            HorizontalLayout drivesUntilLayout = new HorizontalLayout();
            drivesUntilLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
            drivesUntilLayout.add(LineAwesomeIcon.USER_CLOCK_SOLID.create());

            LocalDate drivesUntil = tour.getDrivesUntil();
            if(drivesUntil == null)
            {
                drivesUntilLayout.add(new Text("/"));
            }else drivesUntilLayout.add(new Text(DateTimeUtils.DATE_FORMATTER.format(tour.getDrivesUntil())));

            driverLayout.add(driverNameLayout, drivesUntilLayout);
            layout.add(driverLayout);


            VerticalLayout coDriverLayout = new VerticalLayout();
            coDriverLayout.setPadding(false);
            coDriverLayout.setWidth("100%");

            HorizontalLayout coDriverNameLayout = new HorizontalLayout();
            coDriverNameLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
            coDriverNameLayout.add(LineAwesomeIcon.CAR_ALT_SOLID.create());

            Driver coDriver = tour.getCoDriver();
            if(coDriver == null)
            {
                coDriverNameLayout.add(new Text("/"));
            }else coDriverNameLayout.add(new Text(coDriver.getUser().getName() + " " + coDriver.getUser().getSurname()));

            coDriverLayout.add(coDriverNameLayout);

            LocalDate coDrivesUntil = tour.getCoDrivesUntil();
            HorizontalLayout coDrivesUntilLayout = new HorizontalLayout();
            coDrivesUntilLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
            coDrivesUntilLayout.add(LineAwesomeIcon.USER_CLOCK_SOLID.create());

            if(coDrivesUntil == null)
            {
                coDrivesUntilLayout.add(new Text("/"));
            }else coDrivesUntilLayout.add(new Text(DateTimeUtils.DATE_FORMATTER.format(coDrivesUntil)));

            coDriverLayout.add(coDrivesUntilLayout);

            layout.add(coDriverLayout);

            VerticalLayout institutionsLayout = new VerticalLayout();
            institutionsLayout.setPadding(false);
            institutionsLayout.setWidth("100%");

            tour.getInstitutions().stream()
                    .sorted(Comparator.comparing(tourInstitution -> tourInstitution.getInstitution().getName()))
                    .forEach(tourInstitution ->
                    {
                        HorizontalLayout instLayout = new HorizontalLayout();
                        instLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);

                        instLayout.add(LineAwesomeIcon.BUILDING_SOLID.create(), new Text(tourInstitution.getInstitution().getName()));

                        institutionsLayout.add(instLayout);
                    });

            layout.add(institutionsLayout);
            layout.getStyle()
                    .setMarginLeft(grid.getColumnByKey("details").getWidth());

            Div border = new Div();
            border.add(new Hr(), layout);



            return border;
        });
    }

    private Renderer<Tour> createToggleInstitutionDetailsRenderer(Grid<Tour> grid)
    {
        return LitRenderer
                .<Tour> of("""
                <vaadin-button
                    theme="tertiary icon"
                    aria-label="Toggle details"
                    aria-expanded="${model.detailsOpened ? 'true' : 'false'}"
                    @click="${handleClick}"
                >
                    <vaadin-icon
                    .icon="${model.detailsOpened ? 'lumo:angle-down' : 'lumo:angle-right'}"
                    ></vaadin-icon>
                </vaadin-button>
            """)
                .withFunction("handleClick", selected ->
                {
                    grid.setDetailsVisible(selected, !grid.isDetailsVisible(selected));
                });
    }


    protected static boolean isVehicleOff(LocalDate date, LocalDate startDate, LocalDate endDate)
    {
        if(startDate == null) return false;
        if(endDate == null) endDate = startDate;
        return (date.isEqual(startDate) || date.isEqual(endDate) || (date.isAfter(startDate) && date.isBefore(endDate)));
    }

}
