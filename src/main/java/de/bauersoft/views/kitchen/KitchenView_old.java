package de.bauersoft.views.kitchen;

import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.data.entities.institution.InstitutionMultiplier;
import de.bauersoft.data.entities.institution.InstitutionPattern;
import de.bauersoft.data.entities.order.Order;
import de.bauersoft.data.entities.order.OrderData;
import de.bauersoft.data.entities.pattern.DefaultPattern;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.model.KitchenDTO;
import de.bauersoft.services.*;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

import java.util.*;

//@CssImport(
//        themeFor = "vaadin-grid",
//        value = "./themes/rettels/views/kitchen.css"
//)
//// @CssImport("./themes/rettels/views/kitchen.css")
//@PageTitle("Küchenübersicht")
//@Route(value = "kitchen", layout = MainLayout.class)
//@RolesAllowed("ADMIN")
public class KitchenView_old extends Div {
    private final InstitutionFieldsService institutionFieldsService;
    private KitchenDTO kitchenDTO;
    private Registration pollRegistration;
    private final List<Grid<Order>> grids = new ArrayList<>(); // Liste für alle Grids
    private final Map<Long, Boolean> rowStatusMap = new HashMap<>();
    private final Map<Long, Integer> lastKnownSollWertMap = new HashMap<>();

    public KitchenView_old(OrderService orderService, CourseService courseService,
                           InstitutionService institutionService,
                           InstitutionMultiplierService institutionMultiplierService, PatternService patternService, InstitutionFieldsService institutionFieldsService) {
        this.institutionFieldsService = institutionFieldsService;

        this.kitchenDTO = new KitchenDTO(orderService, institutionFieldsService);

        setClassName("content");
        VerticalLayout pageVerticalLayout = new VerticalLayout();
        pageVerticalLayout.setSizeFull();
        add(pageVerticalLayout);

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();

        //  Erzeuge ein Tab für jeden Course
        for (Course course : courseService.findAll()) {
            Grid<Order> grid = createGrid(course);
            grids.add(grid);
            tabSheet.add(course.getName(), grid);
        }
        // Erstelle ein zusätzliches Tab für "Vegetarisch"
        for (Pattern pattern : patternService.findAll()) {
            if(!DefaultPattern.DEFAULT.equalsDefault(pattern))
            {
                Grid<Order> vegetarischGrid = createPatternGrid(pattern);
                tabSheet.add(pattern.getName(), vegetarischGrid);
                grids.add(vegetarischGrid);
            }
        }

        System.out.println("🔄 [KitchenView] Anzahl registrierter Grids: " + grids.size());

        // evtl für update
        // this.getElement().executeJs("");

        pageVerticalLayout.add(tabSheet);

        // Aktiviert Polling alle 10 Sekunden
        enablePolling();
    }

    private Grid<Order> createPatternGrid(Pattern pattern) {
        Grid<Order> grid = new Grid<>(Order.class, false);

        // Filtere die Bestellungen basierend auf dem Pattern
        List<Order> filteredOrders = new ArrayList<>(kitchenDTO.getLocalDateOrders()
                .stream()
                .filter(order -> order.getOrderData().stream()
                        .anyMatch(orderData -> orderData.getVariant().getPattern().equals(pattern))
                )
                .toList());

        System.out.println("🔍 [Grid] Geladene Orders für Pattern " + pattern.getName() + ": " + filteredOrders.size());

        // Falls keine Orders existieren, füge eine Dummy-Zeile hinzu
        if (filteredOrders.isEmpty()) {
            System.out.println("⚠️ Keine Orders gefunden. Erstelle Dummy-Zeilen für Pattern " + pattern.getName());

            Order dummyOrder = new Order(); // Dummy-Order erstellen
            filteredOrders.add(dummyOrder);
        }

        // Grid mit gefilterten Daten füllen
        grid.setItems(filteredOrders);

        // Spalten dynamisch aus `Order` füllen
        grid.addColumn(order -> {
            if (order.getInstitution() == null) {
                return "Unbekannte Institution - Unbekanntes Feld";
            }
            long institutionId = order.getInstitution().getId();
            long fieldId = order.getField().getId();

            return kitchenDTO.getInstitutionField(institutionId, fieldId)
                    .map(instField -> instField.getInstitution().getName() + " - " + instField.getField().getName())
                    .orElse("Unbekannte Institution - Unbekanntes Feld");
        }).setHeader("Institution - Field");

        grid.addColumn(order -> {
            if (order.getInstitution() == null) {
                return 0;
            }

            Optional<InstitutionField> institutionFieldOptional = institutionFieldsService.findByInstitutionAndField(order.getInstitution(), order.getField());
            if(institutionFieldOptional.isEmpty())
            {
                Notification.show("Es ist ein Fehler aufgetreten! KitchenView#createPatternGrid.institutionFieldOptional");
            }

            int soll = institutionFieldOptional.get().getInstitutionPatterns()
                    .stream()
                    .filter(institutionPattern -> institutionPattern.getPattern().equals(pattern))
                    .map(InstitutionPattern::getAmount)
                    .reduce(0, Integer::sum);

            return soll;
        }).setHeader("Bestellmenge-Soll");

        grid.addColumn(order -> {
            if (order.getInstitution() == null) {
                return 0;
            }

            long institutionId = order.getInstitution().getId();
            long fieldId = order.getField().getId();
            long rowKey = institutionId * 1000 + fieldId; // Eindeutiger Schlüssel für jede Zeile

            // 🔍 Berechne "Bestellmenge-Ist" nur für die aktuelle Institution und das aktuelle Feld
            int neuerSollWert = filteredOrders.stream()
                    .filter(o -> o.getInstitution().getId() == institutionId && o.getField().getId() == fieldId) // Nur passende Orders
                    .flatMap(o -> o.getOrderData().stream()) // Alle OrderData extrahieren
                    .filter(orderData -> orderData.getVariant().getPattern().equals(pattern)) // Nur passendes Pattern
                    .mapToInt(OrderData::getAmount) // Integer-Stream für Summierung
                    .sum(); // Summiere Werte für diese Institution/Feld

            int vorherigerSollWert = lastKnownSollWertMap.getOrDefault(rowKey, -1); // -1 als Default für "noch nicht gesetzt"

            // Falls der Wert noch nie gespeichert wurde, initialisiere ihn
            if (!lastKnownSollWertMap.containsKey(rowKey)) {
                lastKnownSollWertMap.put(rowKey, neuerSollWert);
                rowStatusMap.put(rowKey, false); // Button bleibt deaktiviert
            }

            // Falls sich der Wert geändert hat, Button aktivieren
            if (vorherigerSollWert != -1 && neuerSollWert != vorherigerSollWert) {
                rowStatusMap.put(rowKey, true); // Aktivieren nur, wenn sich etwas geändert hat
                lastKnownSollWertMap.put(rowKey, neuerSollWert); // Aktualisiere den gespeicherten Wert
                System.out.println("🔄 Änderung erkannt! Zeile für " + rowKey + " ist jetzt MODIFIED");
            }

            return neuerSollWert;
        }).setHeader("Bestellmenge-Ist");


        grid.addColumn(new ComponentRenderer<>(order -> {
            if (order.getInstitution() == null) {
                return new Button("N/A");
            }

            long institutionId = order.getInstitution().getId();
            long fieldId = order.getField().getId();
            long rowKey = institutionId * 1000 + fieldId; // Eindeutiger Schlüssel für jede Zeile

            HorizontalLayout layout = new HorizontalLayout();
            Button confirmButton = new Button("Bestätigen");

            confirmButton.setEnabled(rowStatusMap.getOrDefault(rowKey, false)); // Aktiv, falls geändert

            Span checkMark = new Span("✔"); // Grünes Häkchen
            checkMark.getElement().getStyle().set("color", "green");
            checkMark.setVisible(false);

            confirmButton.addClickListener(event -> {
                rowStatusMap.put(rowKey, false); // Status zurücksetzen
                lastKnownSollWertMap.put(rowKey, kitchenDTO.getOrderAmount(institutionId, fieldId).orElse(0)); // Aktualisiert den gespeicherten Wert
                grid.getDataProvider().refreshItem(order); // Grid-Update auslösen
                confirmButton.setEnabled(false);
                checkMark.setVisible(true); // Häkchen anzeigen
            });

            layout.add(confirmButton, checkMark);
            return layout;

        })).setHeader("Bestätigen");

        // Zeilen-Markierung setzen (rot, falls geändert)
        grid.setClassNameGenerator(order -> {
            long institutionId = order.getInstitution() != null ? order.getInstitution().getId() : 0;
            long fieldId = order.getField() != null ? order.getField().getId() : 0;
            long rowKey = institutionId * 1000 + fieldId;

            boolean isModified = rowStatusMap.getOrDefault(rowKey, false);
            System.out.println("🔍 [setClassNameGenerator] Zeile für " + rowKey + ": " + (isModified ? "MODIFIED" : "NORMAL"));

            return isModified ? "warn" : "";
        });

        return grid;
    }


    private Grid<Order> createGrid(Course course) {
        Grid<Order> grid = new Grid<>(Order.class, false);

        // Falls `getFilteredData(course)` nicht alle Orders enthält, nutze kitchenDTO.getLocalDateOrders()
        List<Order> filteredOrders = new ArrayList<>(kitchenDTO.getLocalDateOrders()
                .stream()
                .filter(order -> order.getOrderData().stream()
                        .anyMatch(orderData -> orderData.getVariant().getComponents()
                                .stream()
                                .anyMatch(component -> course.equals(component.getCourse()))
                        )
                )
                .toList()); // Hier konvertieren wir es in eine ArrayList

        System.out.println("🔍 [Grid] Geladene Orders für " + course.getName() + ": " + filteredOrders.size());

        // Falls keine Orders existieren, füge eine Dummy-Zeile hinzu
        if (filteredOrders.isEmpty()) {
            System.out.println("⚠️ Keine Orders gefunden. Erstelle Dummy-Zeilen für " + course.getName());

            Order dummyOrder = new Order(); // Dummy-Order erstellen
            filteredOrders.add(dummyOrder);
        }

        // Grid mit gefilterten Daten füllen
        grid.setItems(filteredOrders);

        // Spalten dynamisch aus `Order` füllen
        grid.addColumn(order -> {
            if (order.getInstitution() == null) {
                return "Unbekannte Institution - Unbekanntes Feld";
            }
            long institutionId = order.getInstitution().getId();
            long fieldId = order.getField().getId();

            return kitchenDTO.getInstitutionField(institutionId, fieldId)
                    .map(instField -> instField.getInstitution().getName() + " - " + instField.getField().getName())
                    .orElse("Unbekannte Institution - Unbekanntes Feld");
        }).setHeader("Institution - Field");

        grid.addColumn(order -> {
            if (order.getInstitution() == null) {
                return 0;
            }

            long institutionId = order.getInstitution().getId();
            long fieldId = order.getField().getId();

            // Solange keine Multiplayer hinterlegt sind!!!!
            // return (int) (orderChildCount * multiplier);
            return kitchenDTO.getChildCount(institutionId, fieldId).orElse(-4);
        }).setHeader("Bestellmenge-Soll");

        grid.addColumn(order -> {
            if (order.getInstitution() == null) {
                return 0;
            }

            long institutionId = order.getInstitution().getId();
            long fieldId = order.getField().getId();
            long rowKey = institutionId * 1000 + fieldId; // Eindeutiger Schlüssel für jede Zeile

            int neuerSollWert = kitchenDTO.getOrderAmount(institutionId, fieldId).orElse(0);
            int vorherigerSollWert = lastKnownSollWertMap.getOrDefault(rowKey, -1); // -1 als Default für "noch nicht gesetzt"

            // 🔥 Falls der Wert noch nie gespeichert wurde, initialisiere ihn
            if (!lastKnownSollWertMap.containsKey(rowKey)) {
                lastKnownSollWertMap.put(rowKey, neuerSollWert);
                rowStatusMap.put(rowKey, false); // 🔥 Button bleibt deaktiviert
            }

            // 🔥 Falls sich der Wert geändert hat, Button aktivieren
            if (vorherigerSollWert != -1 && neuerSollWert != vorherigerSollWert) {
                rowStatusMap.put(rowKey, true); // 🔥 Aktivieren nur, wenn sich etwas geändert hat
                lastKnownSollWertMap.put(rowKey, neuerSollWert); // 🔥 Aktualisiere den gespeicherten Wert
                System.out.println("🔄 Änderung erkannt! Zeile für " + rowKey + " ist jetzt MODIFIED");
            }

                return neuerSollWert;
        }).setHeader("Bestellmenge-Ist");


        grid.addColumn(new ComponentRenderer<>(order -> {
            if (order.getInstitution() == null) {
                return new Button("N/A");
            }

            long institutionId = order.getInstitution().getId();
            long fieldId = order.getField().getId();
            long rowKey = institutionId * 1000 + fieldId; // Eindeutiger Schlüssel für jede Zeile

            HorizontalLayout layout = new HorizontalLayout();
            Button confirmButton = new Button("Bestätigen");

            confirmButton.setEnabled(rowStatusMap.getOrDefault(rowKey, false)); // Aktiv, falls geändert

            Span checkMark = new Span("✔"); // Grünes Häkchen
            checkMark.getElement().getStyle().set("color", "green");
            checkMark.setVisible(false);

            confirmButton.addClickListener(event -> {
                rowStatusMap.put(rowKey, false); // Status zurücksetzen
                lastKnownSollWertMap.put(rowKey, kitchenDTO.getOrderAmount(institutionId, fieldId).orElse(0)); // Aktualisiert den gespeicherten Wert
                grid.getDataProvider().refreshItem(order); // Grid-Update auslösen
                confirmButton.setEnabled(false);
                checkMark.setVisible(true); // Häkchen anzeigen
            });

            layout.add(confirmButton, checkMark);
            return layout;

        })).setHeader("Bestätigen");

        // Zeilen-Markierung setzen (rot, falls geändert)
        grid.setClassNameGenerator(order -> {
            long institutionId = order.getInstitution() != null ? order.getInstitution().getId() : 0;
            long fieldId = order.getField() != null ? order.getField().getId() : 0;
            long rowKey = institutionId * 1000 + fieldId;

            //return rowStatusMap.getOrDefault(rowKey, false) ? "row-modified" : "";

            boolean isModified = rowStatusMap.getOrDefault(rowKey, false);
            System.out.println("🔍 [setClassNameGenerator] Zeile für " + rowKey + ": " + (isModified ? "MODIFIED" : "NORMAL"));

            return isModified ? "warn" : "";
        });

        return grid;
    }

    private void enablePolling() {
        UI.getCurrent().setPollInterval(10000); // Alle 10 Sekunden UI-Aktualisierung

        pollRegistration = UI.getCurrent().addPollListener(event -> {
            System.out.println("🔄 Polling: Aktualisiere Kitchen-Daten...");
            kitchenDTO.updateData();

            grids.forEach(grid -> {
                System.out.println("🔄 [Polling] Aktualisiere Grid für " + grid);
                grid.getDataProvider().refreshAll(); // UI.getCurrent().getPage().reload(); // UI neu laden (alternativ nur Grid aktualisieren)
            });
        });
    }

    // Stoppt das Polling, falls das UI geschlossen wird (z. B. Nutzer verlässt die Seite)
    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        if (pollRegistration != null) {
            pollRegistration.remove();
        }
    }
}

