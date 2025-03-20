package de.bauersoft.views.driver;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.data.entities.tourPlanning.driver.Driver;
import de.bauersoft.services.FleshService;
import de.bauersoft.services.UserService;
import de.bauersoft.services.tourPlanning.DriverService;
import de.bauersoft.services.tourPlanning.TourService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Fahrer")
@Route(value = "driver", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class DriverView extends Div
{
    private final DriverService driverService;
    private final UserService userService;
    private final TourService tourService;

    private final FilterDataProvider<Driver, Long> filterDataProvider;
    private final AutofilterGrid<Driver, Long> grid;

    public DriverView(DriverService driverService, UserService userService, TourService tourService)
    {
        this.driverService = driverService;
        this.userService = userService;
        this.tourService = tourService;

        setClassName("content");

        filterDataProvider = new FilterDataProvider<>(driverService);
        grid = new AutofilterGrid<>(filterDataProvider);

        grid.setWidthFull();
        grid.setHeightFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn("user", "Name", driver ->
        {
            return driver.getUser().getName();

        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
           return criteriaBuilder.like(criteriaBuilder.lower(path.get("name").as(String.class)), filterInput + "%");
        });

        grid.addColumn("user", "Nachname", driver ->
        {
            return driver.getUser().getSurname();
        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.like(criteriaBuilder.lower(path.get("surname").as(String.class)), filterInput + "%");
        });

        grid.addColumn("user", "Emil", driver ->
        {
            return driver.getUser().getEmail();
        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.like(criteriaBuilder.lower(path.get("email").as(String.class)), filterInput + "%");
        });

        grid.AutofilterGridContextMenu()
                .enableGridContextMenu()
                .enableAddItem("Neuer Fahrer", event ->
                {
                    new DriverDialog(filterDataProvider, driverService, userService, tourService, new Driver(), DialogState.NEW);

                }).enableDeleteItem("Löschen", event ->
                {

                });

        grid.addItemDoubleClickListener(event ->
        {
            new DriverDialog(filterDataProvider, driverService, userService, tourService, event.getItem(), DialogState.EDIT);
        });

        this.add(grid);
    }


}
