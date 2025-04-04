package de.bauersoft.views.tour.map;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.data.entities.tour.tour.LatLngPoint;
import de.bauersoft.data.entities.tour.tour.Tour;
import de.bauersoft.data.entities.tour.tour.TourInstitution;
import de.bauersoft.data.entities.tour.tour.TourLocation;
import de.bauersoft.data.repositories.tour.TourRepository;
import de.bauersoft.mobile.broadcaster.InstitutionUpdateBroadcaster;
import de.bauersoft.mobile.broadcaster.LocationBroadcaster;
import de.bauersoft.mobile.model.DTO.TourLocationDTO;
import de.bauersoft.services.tour.TourLocationService;
import de.bauersoft.services.tour.RouteService;
import de.bauersoft.tools.DatePickerLocaleGerman;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import software.xdev.vaadin.maps.leaflet.MapContainer;
import software.xdev.vaadin.maps.leaflet.basictypes.LIconOptions;
import software.xdev.vaadin.maps.leaflet.basictypes.LLatLng;
import software.xdev.vaadin.maps.leaflet.layer.raster.LTileLayer;
import software.xdev.vaadin.maps.leaflet.layer.ui.LMarker;
import software.xdev.vaadin.maps.leaflet.layer.vector.LPolyline;
import software.xdev.vaadin.maps.leaflet.layer.vector.LPolylineOptions;
import software.xdev.vaadin.maps.leaflet.map.LMap;
import software.xdev.vaadin.maps.leaflet.registry.LComponentManagementRegistry;
import software.xdev.vaadin.maps.leaflet.registry.LDefaultComponentManagementRegistry;
import software.xdev.vaadin.maps.leaflet.basictypes.LPoint;


import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

@PageTitle("Touren")
@Route(value = "touroverview", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
@CssImport("./styles/shared-styles.css")
public class TourMap extends VerticalLayout {

    private final LMap map;
    private final LComponentManagementRegistry registry;
    private final TourRepository tourRepository;

    private UI currentUI;
    private Consumer<TourInstitution> institutionListener;

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
        datePicker.setI18n(new DatePickerLocaleGerman());


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
        institutionGrid.addColumn(inst -> inst.getTemperature() != null ? inst.getTemperature() + " °C" : "✘").setHeader("Temperatur").setAutoWidth(true);
        institutionGrid.addColumn(inst ->
                inst.getValidationDateTime() != null
                        ? inst.getValidationDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                        : "✘"
        ).setHeader("Abschluss").setAutoWidth(true);
        institutionGrid.setVisible(false);
        institutionGrid.setHeightFull();
        institutionGrid.setPartNameGenerator(
                inst -> inst.getTemperature() != null ? "validated-row" : "unvalidated-row"
        );

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

    private void switchFilterMode() {
        userComboBox.setVisible(!filterByTourCheckbox.getValue());
        tourComboBox.setVisible(filterByTourCheckbox.getValue());
        if (institutionGrid.isVisible()) {
            MapLayout.setWidth("70%");
            GridLayout.setWidth("30%");
        } else {
            MapLayout.setWidth("100%");
            GridLayout.setWidth("0px"); // Unsichtbar
        }
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
                List<TourInstitution> sortedInstitutions = tour.get().getInstitutions().stream()
                        .sorted(Comparator.comparing(
                                TourInstitution::getValidationDateTime,
                                Comparator.nullsLast(Comparator.naturalOrder()) // Früheste Uhrzeit oben, nulls unten
                        ))
                        .toList();

                institutionGrid.setItems(sortedInstitutions);

                institutionGrid.setVisible(true);

                adjustMapGridLayout(); // ⬅️ Hier hinzufügen
                return;
            }

        }

        institutionGrid.setItems();
        institutionGrid.setVisible(false);
        adjustMapGridLayout(); // ⬅️ Auch hier
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

    @Override
    protected void onAttach(AttachEvent event) {
        this.currentUI = event.getUI(); // ⬅️ UI sichern

        this.locationListener = location -> {
            boolean isSameDate = location.getTimestamp().toLocalDate().equals(datePicker.getValue());

            if (isSameDate && currentUI != null && currentUI.isAttached()) {
                currentUI.access(() -> {
                    System.out.println("[DEBUG] Neuer Standort -> updateUserLocations()");
                    updateUserLocations();
                });
            }
        };
        LocationBroadcaster.register(locationListener);

        this.institutionListener = inst -> {
            getUI().ifPresent(ui -> {
                if (ui.isAttached()) {
                    ui.access(() -> {
                        System.out.println("[DEBUG] Neue Temperatur für Tour " + inst.getTour().getId());
                        updateInstitutionGrid();
                    });
                }
            });
        };
        InstitutionUpdateBroadcaster.register(this.institutionListener);


        this.removalListener = removed -> {
            boolean isSameDate = removed.getTimestamp().toLocalDate().equals(datePicker.getValue());

            if (isSameDate) {
                String key = removed.getTourId() + "-" + removed.getTimestamp();
                LMarker marker = userMarkers.remove(key);
                if (marker != null) {
                    map.removeLayer(marker);
                }

                if (currentUI != null && currentUI.isAttached()) {
                    currentUI.access(() -> {
                        System.out.println("[DEBUG] Standort entfernt -> updateUserLocations()");
                        updateUserLocations();
                    });
                }
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
        if (institutionListener != null) {
            InstitutionUpdateBroadcaster.unregister(institutionListener);
            institutionListener = null;
        }


        this.currentUI = null;
    }

    private void updateUserLocations() {
        clearMapLayers(); // 🧽 Karte leeren

        String selectedValue = filterByTourCheckbox.getValue()
                ? tourComboBox.getValue()
                : userComboBox.getValue();
        boolean filterByTour = filterByTourCheckbox.getValue();
        LocalDate selectedDate = datePicker.getValue();

        List<TourLocationDTO> locations;

        if ("ALLE".equals(selectedValue)) {
            locations = tourLocationService.getLatestTourLocationsByDate(selectedDate);

            // Gruppiere nach Position (gerundet auf 5 Nachkommastellen)
            Map<String, List<TourLocationDTO>> grouped = new HashMap<>();

            for (TourLocationDTO loc : locations) {
                String key = String.format("%.5f_%.5f", loc.getLatitude(), loc.getLongitude());
                grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(loc);
            }

            for (List<TourLocationDTO> group : grouped.values()) {
                TourLocationDTO first = group.get(0);
                LLatLng position = new LLatLng(registry, first.getLatitude(), first.getLongitude());

                StringBuilder popupHtml = new StringBuilder("<div style='font-size: 14px;'>");

                for (TourLocationDTO loc : group) {
                    String time = loc.getTimestamp().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    String date = loc.getTimestamp().toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

                    popupHtml.append("""
                    📍 <strong>%s (ID: %d)</strong><br>
                    🧑 Fahrer: %s<br>
                    🧑 Beifahrer: %s<br>
                    📅 Datum: %s<br>
                    ⏰ %s<br><hr>
                """.formatted(
                            tourLocationService.getTourNameById(loc.getTourId()),
                            loc.getTourId(),
                            tourLocationService.getDriverNameByTourId(loc.getTourId()),
                            tourLocationService.getCoDriverNameByTourId(loc.getTourId()),
                            date,
                            time
                    ));
                }

                popupHtml.append("</div>");

                LMarker marker = createColoredMarker(first, "#80cfff"); // Helles Blau
                marker.bindPopup(popupHtml.toString());
                marker.addTo(map);
                userMarkers.put("grouped-" + first.getTourId() + "-" + first.getLatitude(), marker);
            }

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

            Map<Long, List<TourLocationDTO>> groupedByTour = new HashMap<>();
            for (TourLocationDTO loc : locations) {
                groupedByTour.computeIfAbsent(loc.getTourId(), k -> new ArrayList<>()).add(loc);
            }

            for (Map.Entry<Long, List<TourLocationDTO>> entry : groupedByTour.entrySet()) {
                List<TourLocationDTO> tourLocs = entry.getValue();
                tourLocs.sort(Comparator.comparing(TourLocationDTO::getTimestamp));

                List<LatLngPoint> latLngPoints = new ArrayList<>();
                List<LLatLng> markerPoints = new ArrayList<>();
                List<LatLngPoint> shownPoints = new ArrayList<>();

                for (int i = 0; i < tourLocs.size(); i++) {
                    TourLocationDTO loc = tourLocs.get(i);
                    LatLngPoint current = new LatLngPoint(loc.getLatitude(), loc.getLongitude());
                    boolean isCheckpoint = "X".equalsIgnoreCase(loc.getMarkerIcon());

                    String color;
                    if (i == 0) {
                        color = "#00cc00"; // Start
                    } else if (i == tourLocs.size() - 1) {
                        color = "#cc0000"; // Ende
                    } else if (isCheckpoint) {
                        color = "#ffcc00"; // Checkpoint
                    } else {
                        color = "#80cfff"; // Standard heller Blau
                    }

                    boolean shouldShow = (i == 0 || i == tourLocs.size() - 1 || isCheckpoint);

                    if (!shouldShow) {
                        boolean isNearShownPoint = shownPoints.stream().anyMatch(p ->
                                calculateDistance(p.getLat(), p.getLng(), current.getLat(), current.getLng()) < 50
                        );
                        shouldShow = !isNearShownPoint;
                    }

                    if (shouldShow) {
                        LMarker newMarker = createColoredMarker(loc, color);
                        newMarker.addTo(map);
                        userMarkers.put(loc.getTourId() + "-" + loc.getTimestamp(), newMarker);

                        shownPoints.add(current);
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
        LocalDateTime timestamp = loc.getTimestamp().atZone(ZoneOffset.UTC).toLocalDateTime();
        String institutionID = tourLocationService.getInstitutionIdByLonLatAndTourID(loc.getLatitude(), loc.getLongitude(),loc.getTourId(), timestamp);
        System.out.println(institutionID);
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


    private LMarker createColoredMarker(TourLocationDTO loc, String colorHex)
    {
        // SVG-Vorlage mit CSS-Variablen
        String svg = """
        <svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 500 500'>
            <g id='Layer_x0020_1'>
                <path class='fil0 str0' d='M250 30c77 0 140 63 140 140 0 24-6 46-16 65l-111 227c-2 5-7 8-13 8s-11-3-13-8l-111-227c-10-19-16-41-16-65 0-77 63-140 140-140z' style='--color: red; fill: var(--color); stroke: #343424; stroke-width: 10'/>
                <circle class='fil1 str1' cx='250' cy='170' r='100' style='--eye-color: lightblue; fill: var(--eye-color); stroke: #343424; stroke-width: 10'/>
            </g>
        </svg>
        """;

        // CSS-Variablen ersetzen
        svg = svg.replace("var(--color)", colorHex);
        svg = svg.replace("var(--eye-color)", "background");

        // In Base64 umwandeln
        String base64 = Base64.getEncoder().encodeToString(svg.getBytes(StandardCharsets.UTF_8));
        String dataUri = "data:image/svg+xml;base64," + base64;

        // Icon zusammenbauen
        var iconOptions = new LIconOptions();
        iconOptions.setIconUrl(dataUri);
        iconOptions.setIconSize(new LPoint(registry, 30, 40));      // ggf. anpassen
        iconOptions.setIconAnchor(new LPoint(registry, 15, 40));    // Spitze zeigt auf Position

        var icon = new software.xdev.vaadin.maps.leaflet.basictypes.LIcon(registry, iconOptions);
        var marker = new LMarker(registry, new LLatLng(registry, loc.getLatitude(), loc.getLongitude()));
        marker.setIcon(icon);
        marker.bindPopup(buildPopupHtml(loc));

        return marker;
    }
}