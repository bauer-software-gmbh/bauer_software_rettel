package de.bauersoft.views.vehicle;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.data.entities.tour.vehicle.Vehicle;
import de.bauersoft.data.entities.tour.vehicle.VehicleDowntime;
import de.bauersoft.services.tour.DriverService;
import de.bauersoft.services.tour.VehicleDowntimeService;
import de.bauersoft.services.tour.VehicleService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.Expression;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;

@PageTitle("Fahrzeuge")
@Route(value = "vehicles", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Getter
public class VehicleView extends Div
{
    private static final DateTimeFormatter dateTimeFormatter;

    static
    {
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ROOT);
    }

    private final VehicleService vehicleService;
    private final DriverService driverService;
    private final VehicleDowntimeService vehicleDowntimeService;

    private final FilterDataProvider<Vehicle, Long> filterDataProvider;
    private final AutofilterGrid<Vehicle, Long> grid;

    public VehicleView(VehicleService vehicleService, DriverService driverService, VehicleDowntimeService vehicleDowntimeService)
    {
        this.vehicleService = vehicleService;
        this.vehicleDowntimeService = vehicleDowntimeService;

        setClassName("content");

        filterDataProvider = new FilterDataProvider<>(vehicleService);
        this.driverService = driverService;

        grid = new AutofilterGrid<>(filterDataProvider);

        grid.setWidthFull();
        grid.setHeightFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn("licensePlate", "Nummernschild", Vehicle::getLicensePlate, false);
        grid.addColumn("typeDescription", "Typen Beschreibung", Vehicle::getTypeDescription, false);
        grid.addColumn("downtimes", "Ausfallzeit von", vehicle ->
        {
            return vehicle.getDowntimes().stream()
                    .map(VehicleDowntime::getStartDate)
                    .map(localDate -> dateTimeFormatter.format(localDate))
                    .collect(Collectors.joining(", "));

        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            criteriaQuery.distinct(true);
            return criteriaBuilder.like(
                    criteriaBuilder.function("DATE_FORMAT", String.class, path.get("startDate"), criteriaBuilder.literal("%d.%m.%Y")),
                    filterInput + "%"
            );
        }).enableSorting(false);

        grid.addColumn("downtimes", "Ausfallzeit bis", vehicle ->
        {
            return vehicle.getDowntimes().stream()
                    .map(downtime -> downtime.getEndDate() != null ? downtime.getEndDate() : downtime.getStartDate())
                    .map(localDate -> dateTimeFormatter.format(localDate))
                    .collect(Collectors.joining(", "));

        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            criteriaQuery.distinct(true);
            Expression<?> date = criteriaBuilder.selectCase()
                    .when(criteriaBuilder.isNull(path.get("endDate")), path.get("startDate"))
                    .otherwise(path.get("endDate"));

            return criteriaBuilder.like(
                    criteriaBuilder.function("DATE_FORMAT", String.class, date, criteriaBuilder.literal("%d.%m.%Y")),
                    filterInput + "%"
            );
        }).enableSorting(false);

        grid.AutofilterGridContextMenu().enableGridContextMenu()
                        .enableAddItem("Neues Fahrzeug", event ->
                        {
                            new VehicleDialog(filterDataProvider, vehicleService, driverService, vehicleDowntimeService, new Vehicle(), DialogState.NEW);
                        }).enableDeleteItem("Löschen", event ->
                        {
                            event.getItem().ifPresent(vehicle ->
                            {
                                vehicleService.delete(vehicle);
                                filterDataProvider.refreshAll();
                            });
                        });

        grid.addItemDoubleClickListener(event ->
        {
            new VehicleDialog(filterDataProvider, vehicleService, driverService, vehicleDowntimeService, event.getItem(), DialogState.EDIT);
        });

        this.add(grid);
    }
}
