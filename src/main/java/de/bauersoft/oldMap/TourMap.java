package de.bauersoft.oldMap;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.data.entities.tour.tour.LatLngPoint;
import de.bauersoft.data.entities.tour.tour.Tour;
import de.bauersoft.data.entities.tour.tour.TourInstitution;
import de.bauersoft.data.repositories.tour.TourRepository;
import de.bauersoft.mobile.broadcaster.LocationBroadcaster;
import de.bauersoft.services.tour.RouteService;
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
@Route(value = "touroverview", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class TourMap extends VerticalLayout {

    private final LMap map;
    private final LComponentManagementRegistry registry;
    private final TourRepository tourRepository;

    private final Checkbox filterByTourCheckbox = new Checkbox("Nach Tour filtern");
    private final ComboBox<String> userComboBox = new ComboBox<>("Fahrer auswählen");
    private final ComboBox<String> tourComboBox = new ComboBox<>("Tour auswählen");
    private final DatePicker datePicker = new DatePicker("Datum auswählen");

    private final Map<String, LMarker> userMarkers = new HashMap<>();
    private final List<LPolyline> routeLines = new ArrayList<>();

    private final Grid<TourInstitution> institutionGrid = new Grid<>(TourInstitution.class, false);

    private Consumer<TourLocationDTO> locationListener;
    private Consumer<TourLocationDTO> removalListener;

    private HorizontalLayout MapLayout, GridLayout;

    private final TourLocationService tourLocationService;

    public TourMap(TourRepository tourRepository, TourLocationService tourLocationService) {
        this.tourRepository = tourRepository;
        this.tourLocationService = tourLocationService;
        this.registry = new LDefaultComponentManagementRegistry(this);

        setSizeFull();
        setPadding(false);
        getStyle().set("background", "white");

        MapContainer mapContainer = new MapContainer(registry);
        mapContainer.setWidthFull();
        mapContainer.setHeightFull();

        this.map = mapContainer.getlMap();
        LTileLayer tileLayer = LTileLayer.createDefaultForOpenStreetMapTileServer(registry);
        tileLayer.addTo(map);
        map.setView(new LLatLng(registry, 49.3517, 6.8054), 14);

        userComboBox.setPlaceholder("Bitte Fahrer auswählen");
        tourComboBox.setPlaceholder("Bitte Tour auswählen");
        datePicker.setValue(LocalDate.now());
        datePicker.setPlaceholder("Datum auswählen");

        filterByTourCheckbox.addValueChangeListener(event -> switchFilterMode());
        userComboBox.addValueChangeListener(event -> {
            updateUserLocations();
            updateInstitutionGrid();
        });
        tourComboBox.addValueChangeListener(event -> {
            updateUserLocations();
            updateInstitutionGrid();
        });
        datePicker.addValueChangeListener(event -> updateUserLocations());

        HorizontalLayout filterLayout = new HorizontalLayout(datePicker, userComboBox, tourComboBox, filterByTourCheckbox);
        filterLayout.setWidthFull();
        filterLayout.setAlignItems(Alignment.CENTER);
        filterLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        institutionGrid.addColumn(inst -> inst.getInstitution().getName()).setHeader("Institution").setAutoWidth(true);
        institutionGrid.addColumn(inst -> inst.getTemperatureImage() != null ? "✓" : "✘").setHeader("Temperatur").setAutoWidth(true);
        institutionGrid.addColumn(inst -> inst.getValidationTime()).setHeader("Validierung").setAutoWidth(true);
        institutionGrid.setVisible(false);
        institutionGrid.setHeightFull();

        MapLayout = new HorizontalLayout();
        MapLayout.setWidthFull();
        MapLayout.setHeightFull();
        MapLayout.add(mapContainer);

        GridLayout = new HorizontalLayout();
        GridLayout.setHeightFull();
        GridLayout.setWidthFull();
        GridLayout.add(institutionGrid);

        HorizontalLayout MapGridLayout = new HorizontalLayout();
        MapGridLayout.setHeightFull();
        MapGridLayout.setWidthFull();
        MapGridLayout.add(MapLayout, GridLayout);

        add(filterLayout, MapGridLayout);

        switchFilterMode();
        updateUserLocations();
    }

    private void adjustMapGridLayout()
    {
        if (institutionGrid.isVisible()) {
            MapLayout.setWidth("70%");
            GridLayout.setWidth("30%");
        } else {
            MapLayout.setWidth("100%");
            GridLayout.setWidth("0px"); // Unsichtbar
        }
    }

    private void switchFilterMode() {
        userComboBox.setVisible(!filterByTourCheckbox.getValue());
        tourComboBox.setVisible(filterByTourCheckbox.getValue());
        userComboBox.clear();
        tourComboBox.clear();

        List<Tour> tours = tourRepository.findAll();
        Set<String> userOptions = new LinkedHashSet<>();
        List<String> tourOptions = new ArrayList<>();
        userOptions.add("ALLE");
        tourOptions.add("ALLE");

        for (Tour tour : tours) {
            if (tour.getDriver() != null) {
                var user = tour.getDriver().getUser();
                userOptions.add(user.getId() + " - " + user.getName() + " " + user.getSurname());
            }
            if (tour.getCoDriver() != null) {
                var user = tour.getCoDriver().getUser();
                userOptions.add(user.getId() + " - " + user.getName() + " " + user.getSurname());
            }
            tourOptions.add(tour.getId() + " - " + tour.getName());
        }

        userComboBox.setItems(userOptions);
        tourComboBox.setItems(tourOptions);

        userComboBox.setValue("ALLE");
        tourComboBox.setValue("ALLE");

        adjustMapGridLayout();
        updateUserLocations();
        updateInstitutionGrid();
    }

    private void updateInstitutionGrid() {
        String selectedValue = filterByTourCheckbox.getValue()
                ? tourComboBox.getValue()
                : userComboBox.getValue();
        boolean filterByTour = filterByTourCheckbox.getValue();

        if (!"ALLE".equals(selectedValue) && filterByTour) {
            Long tourId = Long.parseLong(selectedValue.split(" - ")[0]);
            Optional<Tour> tour = tourRepository.findById(tourId);
            if (tour.isPresent()) {
                List<TourInstitution> institutions = tour.get().getInstitutions().stream().toList();
                institutionGrid.setItems(institutions);
                institutionGrid.setVisible(true);

                adjustMapGridLayout(); // ⬅️ Hier hinzufügen
                return;
            }
        }

        institutionGrid.setItems();
        institutionGrid.setVisible(false);
        adjustMapGridLayout(); // ⬅️ Auch hier
    }


    @Override
    protected void onAttach(AttachEvent event) {
        this.locationListener = location -> {
            boolean isSameDate = location.getTimestamp().toLocalDate().equals(datePicker.getValue());
            if (isSameDate) {
                getUI().ifPresent(ui -> ui.access(this::updateUserLocations));
            }
        };
        LocationBroadcaster.register(locationListener);

        this.removalListener = removed -> {
            boolean isSameDate = removed.getTimestamp().toLocalDate().equals(datePicker.getValue());
            if (isSameDate) {
                String key = removed.getTourId() + "-" + removed.getTimestamp();
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
    protected void onDetach(DetachEvent event) {
        if (this.locationListener != null) {
            LocationBroadcaster.unregister(this.locationListener);
            this.locationListener = null;
        }
        if (this.removalListener != null) {
            LocationBroadcaster.unregisterRemovalListener(this.removalListener);
            this.removalListener = null;
        }
    }

    private void updateUserLocations()
    {

        clearMapLayers(); // 🧽 immer Karte leeren

        String selectedValue = filterByTourCheckbox.getValue()
                ? tourComboBox.getValue()
                : userComboBox.getValue();
        boolean filterByTour = filterByTourCheckbox.getValue();
        LocalDate selectedDate = datePicker.getValue();

        List<TourLocationDTO> locations;

        if ("ALLE".equals(selectedValue)) {
            locations = tourLocationService.getLatestTourLocationsByDate(selectedDate); // 🔄 Nur die letzten!
        } else {
            locations = tourLocationService.getAllTourLocations().stream()
                    .filter(loc -> loc.getTimestamp().toLocalDate().equals(selectedDate))
                    .filter(loc -> {
                        if (filterByTour) {
                            Long selectedTourId = Long.parseLong(selectedValue.split(" - ")[0]);
                            return loc.getTourId().equals(selectedTourId);
                        } else {
                            Long selectedUserId = Long.parseLong(selectedValue.split(" - ")[0]);
                            return tourRepository.findAll().stream().anyMatch(tour ->
                                    tour.getId().equals(loc.getTourId()) &&
                                            (
                                                    (tour.getDriver() != null && tour.getDriver().getUser().getId().equals(selectedUserId)) ||
                                                            (tour.getCoDriver() != null && tour.getCoDriver().getUser().getId().equals(selectedUserId))
                                            )
                            );
                        }
                    })
                    .toList();
        }

        if ("ALLE".equals(selectedValue)) {
            for (TourLocationDTO loc : locations) {
                LLatLng pos = new LLatLng(registry, loc.getLatitude(), loc.getLongitude());
                LMarker marker = new LMarker(registry, pos);

                marker.bindPopup(buildPopupHtml(loc));
                marker.addTo(map);
                userMarkers.put("latest-" + loc.getTourId(), marker);
            }
        } else {
            Map<Long, List<TourLocationDTO>> groupedByTour = new HashMap<>();
            for (TourLocationDTO loc : locations) {
                groupedByTour.computeIfAbsent(loc.getTourId(), k -> new ArrayList<>()).add(loc);
            }

            for (Map.Entry<Long, List<TourLocationDTO>> entry : groupedByTour.entrySet()) {
                List<TourLocationDTO> tourLocs = entry.getValue();
                tourLocs.sort(Comparator.comparing(TourLocationDTO::getTimestamp));

                List<LatLngPoint> latLngPoints = new ArrayList<>();
                List<LLatLng> markerPoints = new ArrayList<>();
                LatLngPoint lastAcceptedPoint = null;

                for (TourLocationDTO loc : tourLocs) {
                    LatLngPoint current = new LatLngPoint(loc.getLatitude(), loc.getLongitude());

                    if (lastAcceptedPoint == null ||
                            calculateDistance(lastAcceptedPoint.getLat(), lastAcceptedPoint.getLng(), current.getLat(), current.getLng()) > 50) {
                        LMarker newMarker = getLMarker(loc);
                        newMarker.addTo(map);
                        userMarkers.put(loc.getTourId() + "-" + loc.getTimestamp(), newMarker);

                        lastAcceptedPoint = current;
                        latLngPoints.add(current);
                        markerPoints.add(new LLatLng(registry, current.getLat(), current.getLng()));
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
    }

    private void clearMapLayers() {
        userMarkers.values().forEach(map::removeLayer);
        userMarkers.clear();

        for (LPolyline line : routeLines) {
            map.removeLayer(line);
        }
        routeLines.clear();
    }

    private String buildPopupHtml(TourLocationDTO loc)
    {
        String tourName = tourLocationService.getTourNameById(loc.getTourId());
        String formattedDate = loc.getTimestamp().toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String formattedTime = loc.getTimestamp().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        return """
            <div style='line-height: 1.4em; font-size: 14px;'>
            📍 <strong>%s (Tour ID: %d)</strong><br>
            🧑 <strong>Fahrer:</strong> %s<br>
            🧑 <strong>Beifahrer:</strong> %s<br>
            📅 <strong>Datum:</strong> %s<br>
            ⏰ <strong>Uhrzeit:</strong> %s
            </div>
            """.formatted(
                tourName,
                loc.getTourId(),
                tourLocationService.getDriverNameByTourId(loc.getTourId()),
                tourLocationService.getCoDriverNameByTourId(loc.getTourId()),
                formattedDate,
                formattedTime
        );
    }



    private LMarker getLMarker(TourLocationDTO loc)
    {
        LLatLng userPosition = new LLatLng(registry, loc.getLatitude(), loc.getLongitude());
        LMarker newMarker = new LMarker(registry, userPosition);

        String popupHtml = buildPopupHtml(loc);
        newMarker.bindPopup(popupHtml);

        return newMarker;
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2)
    {
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