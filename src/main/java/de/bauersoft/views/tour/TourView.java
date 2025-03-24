package de.bauersoft.views.tour;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.tourPlanning.driver.Driver;
import de.bauersoft.data.entities.tourPlanning.tour.Tour;
import de.bauersoft.data.entities.tourPlanning.tour.TourInstitution;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.services.InstitutionService;
import de.bauersoft.services.tourPlanning.*;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;

import java.util.stream.Collectors;

@PageTitle("Tourenplanung")
@Route(value = "tour", layout = MainLayout.class)
@RolesAllowed("ADMIN")
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

        grid.setHeightFull();
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn("name", "Name", Tour::getName, false);
        grid.addColumn("vehicle", "Fahrzeug", tour ->
        {
            return tour.getVehicle().getLicensePlate();
        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.like(criteriaBuilder.lower(path.get("licensePlate")), "%" + filterInput.toLowerCase() + "%");
        });
        grid.addColumn("requiredDrivers", "Benötigte Fahrer", tour -> String.valueOf(tour.getRequiredDrivers()), false);
        grid.addColumn("driver", "Fahrer", tour ->
        {
            User user = tour.getDriver().getUser();
            return user.getName() + " " + user.getSurname();

        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(path.get("user").get("name")), filterInput.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(path.get("user").get("surname")), filterInput.toLowerCase() + "%")
            );
        });

        grid.addColumn("coDriver", "Beifahrer", tour ->
        {
            Driver coDriver = tour.getCoDriver();
            if(coDriver == null) return "";

            User user = coDriver.getUser();
            return (user == null) ? "" : user.getName() + " " + user.getSurname();

        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(path.get("user").get("name")), filterInput.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(path.get("user").get("surname")), filterInput.toLowerCase() + "%")
            );
        });

        grid.addColumn("institutions", "Institutionen", tour ->
        {
            return tour.getInstitutions().stream()
                    .map(TourInstitution::getInstitution)
                    .map(Institution::getName
                    ).collect(Collectors.joining(", "));

        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.like(criteriaBuilder.lower(path.get("institution").get("name")), filterInput.toLowerCase() + "%");
        }).enableSorting(false);

        grid.AutofilterGridContextMenu().enableGridContextMenu()
                        .enableAddItem("Neue Tour", event ->
                        {
                            new TourDialog(filterDataProvider, tourService, driverService, vehicleService, vehicleDowntimeService, institutionService, tourInstitutionService, new Tour(), DialogState.NEW);
                        }).enableDeleteItem("Löschen", event ->
                        {

                        });

        grid.addItemDoubleClickListener(event ->
        {
            new TourDialog(filterDataProvider, tourService, driverService, vehicleService, vehicleDowntimeService, institutionService, tourInstitutionService, event.getItem(), DialogState.EDIT);
        });

        this.add(grid);
    }
}
