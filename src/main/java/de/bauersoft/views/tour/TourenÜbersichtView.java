package de.bauersoft.views.tour;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.data.entities.tourPlanning.driver.Driver;
import de.bauersoft.data.entities.tourPlanning.tour.LatLngPoint;
import de.bauersoft.data.repositories.tourPlanning.DriverRepository;
import de.bauersoft.mobile.broadcaster.LocationBroadcaster;
import de.bauersoft.mobile.model.DTO.UserLocationDTO;
import de.bauersoft.services.UserLocationService;
import de.bauersoft.services.tourPlanning.RouteService;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import software.xdev.vaadin.maps.leaflet.MapContainer;
import software.xdev.vaadin.maps.leaflet.basictypes.LLatLng;
import software.xdev.vaadin.maps.leaflet.layer.raster.LTileLayer;
import software.xdev.vaadin.maps.leaflet.layer.ui.LMarker;
import software.xdev.vaadin.maps.leaflet.layer.vector.LPolyline;
import software.xdev.vaadin.maps.leaflet.layer.vector.LPolylineOptions;
import software.xdev.vaadin.maps.leaflet.map.LMap;
import software.xdev.vaadin.maps.leaflet.registry.LComponentManagementRegistry;
import software.xdev.vaadin.maps.leaflet.registry.LDefaultComponentManagementRegistry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

@PageTitle("Touren")
@Route(value = "tours", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "KITCHEN", "KITCHEN_ADMIN", "OFFICE", "OFFICE_ADMIN"})
public class TourenÜbersichtView extends VerticalLayout {

    private final LMap map;
    private final LComponentManagementRegistry registry;
    private final Map<String, LMarker> userMarkers = new HashMap<>();
    private final UserLocationService userLocationService;
    private final DriverRepository driverRepository;
    private final List<LPolyline> routeLines = new ArrayList<>();

    private ComboBox<String> driverDropdown;
    private DatePicker datePicker;
    private Consumer<UserLocationDTO> locationListener;
    private Consumer<UserLocationDTO> removalListener;

    public TourenÜbersichtView(UserLocationService userLocationService, DriverRepository driverRepository) {
        this.userLocationService = userLocationService;
        this.driverRepository = driverRepository;
        this.registry = new LDefaultComponentManagementRegistry(this);

        setSizeFull();
        setPadding(false);
        getStyle().set("background", "white");

        MapContainer mapContainer = new MapContainer(registry);
        mapContainer.setWidthFull();
        mapContainer.setHeightFull();
        add(mapContainer);

        this.map = mapContainer.getlMap();
        LTileLayer tileLayer = LTileLayer.createDefaultForOpenStreetMapTileServer(registry);
        tileLayer.addTo(map);
        map.setView(new LLatLng(registry, 49.3517, 6.8054), 14);

        driverDropdown = new ComboBox<>("Fahrer auswählen");
        List<Driver> drivers = driverRepository.findAll();
        List<String> driverNames = new ArrayList<>();
        driverNames.add("ALLE");
        drivers.forEach(driver -> driverNames.add(driver.getUser().getId() + " - " + driver.getUser().getName() + " " + driver.getUser().getSurname()));

        driverDropdown.setItems(driverNames);
        driverDropdown.setPlaceholder("Bitte Fahrer auswählen");
        driverDropdown.setValue("ALLE");

        datePicker = new DatePicker("Datum auswählen");
        datePicker.setValue(LocalDate.now());
        datePicker.setPlaceholder("Datum auswählen");

        driverDropdown.addValueChangeListener(event -> updateUserLocations());
        datePicker.addValueChangeListener(event -> updateUserLocations());

        HorizontalLayout filterLayout = new HorizontalLayout(driverDropdown, datePicker);
        filterLayout.setWidthFull();
        filterLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        add(filterLayout, mapContainer);

        updateUserLocations();
    }

    @Override
    public void onAttach(AttachEvent event) {
        this.locationListener = location -> {
            boolean isCorrectUser = "ALLE".equals(driverDropdown.getValue())
                    || location.getUserId().equals(Long.parseLong(driverDropdown.getValue().split(" - ")[0]));
            boolean isSameDate = location.getTimestamp().toLocalDate().equals(datePicker.getValue());

            if (isCorrectUser && isSameDate) {
                getUI().ifPresent(ui -> ui.access(this::updateUserLocations));
            }
        };
        LocationBroadcaster.register(locationListener);

        this.removalListener = removed -> {
            boolean isCorrectUser = "ALLE".equals(driverDropdown.getValue())
                    || removed.getUserId().equals(Long.parseLong(driverDropdown.getValue().split(" - ")[0]));
            boolean isSameDate = removed.getTimestamp().toLocalDate().equals(datePicker.getValue());

            if (isCorrectUser && isSameDate) {
                String key = removed.getUserId() + "-" + removed.getTimestamp();
                LMarker marker = userMarkers.remove(key);
                if (marker != null) {
                    map.removeLayer(marker);
                }
                getUI().ifPresent(ui -> ui.access(this::updateUserLocations));
            }
        };
        LocationBroadcaster.registerRemovalListener(removalListener);
    }

    @Override
    public void onDetach(DetachEvent event) {
        if (this.locationListener != null) {
            LocationBroadcaster.unregister(this.locationListener);
            this.locationListener = null;
        }
        if (this.removalListener != null) {
            LocationBroadcaster.unregisterRemovalListener(this.removalListener);
            this.removalListener = null;
        }
    }

    private void updateUserLocations() {
        String selectedUser = driverDropdown.getValue();
        LocalDate selectedDate = datePicker.getValue();

        userMarkers.values().forEach(marker -> map.removeLayer(marker));
        userMarkers.clear();

        for (LPolyline line : routeLines) {
            map.removeLayer(line);
        }
        routeLines.clear();

        List<UserLocationDTO> locations = "ALLE".equals(selectedUser)
                ? userLocationService.getLatestUserLocationsByDate(selectedDate)
                : userLocationService.getUserLocationsByDate(Long.parseLong(selectedUser.split(" - ")[0]), selectedDate);

        Map<Long, List<UserLocationDTO>> groupedByUser = new HashMap<>();
        for (UserLocationDTO loc : locations) {
            groupedByUser.computeIfAbsent(loc.getUserId(), k -> new ArrayList<>()).add(loc);
        }

        for (Map.Entry<Long, List<UserLocationDTO>> entry : groupedByUser.entrySet()) {
            List<UserLocationDTO> userLocs = entry.getValue();
            userLocs.sort(Comparator.comparing(UserLocationDTO::getTimestamp));

            List<LatLngPoint> latLngPoints = new ArrayList<>();
            List<LLatLng> markerPoints = new ArrayList<>();
            LatLngPoint lastAcceptedPoint = null;

            for (UserLocationDTO loc : userLocs) {
                LatLngPoint current = new LatLngPoint(loc.getLatitude(), loc.getLongitude());

                if (lastAcceptedPoint == null ||
                        calculateDistance(lastAcceptedPoint.lat, lastAcceptedPoint.lng, current.lat, current.lng) > 50) {

                    LMarker newMarker = getLMarker(loc);
                    newMarker.addTo(map);
                    userMarkers.put(loc.getUserId() + "-" + loc.getTimestamp(), newMarker);

                    lastAcceptedPoint = current;
                    latLngPoints.add(current);
                    markerPoints.add(new LLatLng(registry, current.lat, current.lng));
                }
            }

            if (latLngPoints.size() > 1) {
                try {
                    List<LLatLng> routedPath = RouteService.fetchRoute(latLngPoints, registry);
                    LPolylineOptions options = new LPolylineOptions();
                    options.setColor("#0077cc");
                    options.setWeight(5);

                    LPolyline line = new LPolyline(registry, routedPath, options);
                    line.addTo(map);
                    routeLines.add(line);
                } catch (Exception e) {
                    e.printStackTrace();
                    LPolyline fallbackLine = new LPolyline(registry, markerPoints);
                    fallbackLine.addTo(map);
                    routeLines.add(fallbackLine);
                }
            }
        }
    }

    private LMarker getLMarker(UserLocationDTO loc) {
        LLatLng userPosition = new LLatLng(registry, loc.getLatitude(), loc.getLongitude());
        LMarker newMarker = new LMarker(registry, userPosition);
        String fullname = userLocationService.getFullNameByUserId(loc.getUserId());
        String formattedDate = loc.getTimestamp().toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String formattedTime = loc.getTimestamp().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        String popupHtml = """
            <div style='line-height: 1.4em; font-size: 14px;'>
            📍 <strong>Fahrer:</strong> %s (ID: %d)<br>
            🗓 <strong>Datum:</strong> %s<br>
            ⏰ <strong>Uhrzeit:</strong> %s
            </div>
        """.formatted(fullname, loc.getUserId(), formattedDate, formattedTime);
        newMarker.bindPopup(popupHtml);
        return newMarker;
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
