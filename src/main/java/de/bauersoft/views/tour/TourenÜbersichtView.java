package de.bauersoft.views.tour;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.data.entities.tourPlanning.driver.Driver;
import de.bauersoft.data.repositories.tourPlanning.DriverRepository;
import de.bauersoft.mobile.model.DTO.UserLocationDTO;
import de.bauersoft.services.UserLocationService;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import software.xdev.vaadin.maps.leaflet.MapContainer;
import software.xdev.vaadin.maps.leaflet.basictypes.LLatLng;
import software.xdev.vaadin.maps.leaflet.layer.raster.LTileLayer;
import software.xdev.vaadin.maps.leaflet.layer.ui.LMarker;
import software.xdev.vaadin.maps.leaflet.map.LMap;
import software.xdev.vaadin.maps.leaflet.registry.LComponentManagementRegistry;
import software.xdev.vaadin.maps.leaflet.registry.LDefaultComponentManagementRegistry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PageTitle("Touren")
@Route(value = "tours", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "KITCHEN", "KITCHEN_ADMIN", "OFFICE", "OFFICE_ADMIN"})
public class TourenÜbersichtView extends VerticalLayout {

    private final LMap map;
    private final LComponentManagementRegistry registry;
    private final Map<String, LMarker> userMarkers = new HashMap<>();
    private final UserLocationService userLocationService;
    private final DriverRepository driverRepository;

    private ComboBox<String> driverDropdown;
    private DatePicker datePicker;

    public TourenÜbersichtView(UserLocationService userLocationService, DriverRepository driverRepository) {
        this.userLocationService = userLocationService;
        this.driverRepository = driverRepository;
        this.registry = new LDefaultComponentManagementRegistry(this);

        // 🔥 Hintergrund auf Weiß setzen
        setSizeFull();
        setPadding(false);
        getStyle().set("background", "white");

        // 🗺️ Map-Container
        MapContainer mapContainer = new MapContainer(registry);
        mapContainer.setWidthFull();
        mapContainer.setHeightFull();
        add(mapContainer);

        this.map = mapContainer.getlMap();
        LTileLayer tileLayer = LTileLayer.createDefaultForOpenStreetMapTileServer(registry);
        tileLayer.addTo(map);
        map.setView(new LLatLng(registry, 49.3517, 6.8054), 14);

        // 🚛 Dropdown für Fahrer (mit "ALLE" Option)
        driverDropdown = new ComboBox<>("Fahrer auswählen");
        List<Driver> drivers = driverRepository.findAll();
        List<String> driverNames = new ArrayList<>();
        driverNames.add("ALLE");
        drivers.forEach(driver -> driverNames.add(driver.getUser().getId() + " - " + driver.getUser().getName() + " " + driver.getUser().getSurname()));

        driverDropdown.setItems(driverNames);
        driverDropdown.setPlaceholder("Bitte Fahrer auswählen");
        driverDropdown.setValue("ALLE"); // Standard auf "ALLE"

        // 📅 DatePicker für Datumsauswahl
        datePicker = new DatePicker("Datum auswählen");
        datePicker.setValue(LocalDate.now()); // Standard heute
        datePicker.setPlaceholder("Datum auswählen");

        // 🔥 ValueChangeListener → Automatische Aktualisierung ohne Button
        driverDropdown.addValueChangeListener(event -> updateUserLocations());
        datePicker.addValueChangeListener(event -> updateUserLocations());

        // 🛠 Layout für Dropdowns (NEBENEINANDER!)
        HorizontalLayout filterLayout = new HorizontalLayout(driverDropdown, datePicker);
        filterLayout.setWidthFull();
        filterLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        add(filterLayout, mapContainer);

        updateUserLocations();
    }

    private void updateUserLocations() {
        String selectedUser = driverDropdown.getValue();
        LocalDate selectedDate = datePicker.getValue();

        // 🔥 ALLE Marker vorher löschen
        userMarkers.values().forEach(marker -> map.removeLayer(marker));
        userMarkers.clear();

        List<UserLocationDTO> locations;

        if ("ALLE".equals(selectedUser)) {
            // ✅ Falls "ALLE" gewählt wurde, nur die neuesten Standorte pro Nutzer abrufen
            locations = userLocationService.getLatestUserLocationsByDate(selectedDate);
        } else {
            // ✅ Falls ein einzelner Nutzer gewählt wurde, nur diesen abrufen
            Long userId = Long.parseLong(selectedUser.split(" - ")[0]);
            locations = userLocationService.getUserLocationsByDate(userId, selectedDate);
        }

        for (UserLocationDTO loc : locations) {
            LMarker newMarker = getLMarker(loc);
            newMarker.addTo(map);
            userMarkers.put(loc.getUserId() + "-" + loc.getTimestamp(), newMarker);
        }
    }

    private LMarker getLMarker(UserLocationDTO loc) {
        LLatLng userPosition = new LLatLng(registry, loc.getLatitude(), loc.getLongitude());
        LMarker newMarker = new LMarker(registry, userPosition);

        // 🔥 User-Vollname abrufen
        String fullname = userLocationService.getFullNameByUserId(loc.getUserId());

        // 🔥 User-Infos aufsplitten (ID + Name, Datum, Uhrzeit)
        String userInfo = "📍 Fahrer: " + fullname + " (ID: " + loc.getUserId() + ")";
        String dateInfo = "📅 Datum: " + loc.getTimestamp().toLocalDate();
        String timeInfo = "⏰ Uhrzeit: " + loc.getTimestamp().toLocalTime();

        // 🔥 Popup mit den getrennten Infos
        newMarker.bindPopup(userInfo + "<br>" + dateInfo + "<br>" + timeInfo);
        return newMarker;
    }
}
