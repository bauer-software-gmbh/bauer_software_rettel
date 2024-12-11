package de.bauersoft.views.offers;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.bauersoft.data.entities.Menu;
import de.bauersoft.data.entities.Week;
import de.bauersoft.data.repositories.menu.MenuRepository;
import de.bauersoft.tools.DatePickerLocaleGerman;
import de.bauersoft.views.MainLayout;
import com.vaadin.flow.component.html.Div;
import org.apache.commons.lang3.IntegerRange;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Function;

@PageTitle("offers")
@Route(value = "offers", layout = MainLayout.class)
@AnonymousAllowed
public class OffersView extends Div {
    private final ListDataProvider<Week> dataProvider;

    public OffersView(MenuRepository menuRepository) {
        setClassName("content");
        VerticalLayout pageVerticalLayout = new VerticalLayout();
        pageVerticalLayout.setSizeFull();

        // Filter-Komponente

        IntegerField filterWeek = new IntegerField("Wochenanzahl:");
        filterWeek.setMin(1); // Minimale Anzahl der Wochen
        filterWeek.setMax(53); // Maximale Anzahl der Wochen
        filterWeek.setStep(1); // Schritte in Ganzzahlen
        filterWeek.setValue(4); // Standardwert

        filterWeek.addValueChangeListener(event -> {
            if (event.getValue() != null && IntegerRange.of(1,53).contains(event.getValue())) {
                updateGrid(event.getValue());
            }
        });

        DatePicker filterDate = new DatePicker("Datum:");
        filterDate.setValue(LocalDate.now());
        filterDate.setWeekNumbersVisible(true);
        filterDate.setI18n(new DatePickerLocaleGerman());


        // Startdatum und initiale Daten
        LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        int numberOfWeeks = filterWeek.getValue() != null ? filterWeek.getValue() : 10; // Standardwert 10
        List<Week> weeks = generateWeeks(startOfWeek, numberOfWeeks);
        dataProvider = new ListDataProvider<>(weeks);

        // Grid-Setup
        Grid<Week> grid = new Grid<>(Week.class, false);

        // KW-Spalte hinzufügen
        addKWColumn(grid);

        Map<String, Function<Week, LocalDate>> dayMap = new LinkedHashMap<>();
        dayMap.put("Montag", Week::getMon);
        dayMap.put("Dienstag", Week::getTue);
        dayMap.put("Mittwoch", Week::getWed);
        dayMap.put("Donnerstag", Week::getThu);
        dayMap.put("Freitag", Week::getFri);
        dayMap.put("Samstag", Week::getSat);
        dayMap.put("Sonntag", Week::getSun);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        dayMap.forEach((header, dateMethod) -> addDayColumn(grid, header, dateMethod, formatter));
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setDataProvider(dataProvider);
        grid.setSizeFull();

        HorizontalLayout oben  = new HorizontalLayout();
        oben.add(filterWeek, filterDate);
        oben.setAlignItems(FlexComponent.Alignment.CENTER); // Alles zentrieren

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();


        VirtualList<Menu> virtualList = new VirtualList<>();
        virtualList.setRenderer(new ComponentRenderer<Div,Menu>(component->{
            MenuDiv container = new MenuDiv(component);
            DragSource.create(container);
            return container;
        }));
        List<Menu> list = menuRepository.findAll();
        ListDataProvider<Menu> provider = new ListDataProvider<>(list);

        DropTarget.create(virtualList).addDropListener(event-> event.getDragSourceComponent().ifPresent(source->{
                    if(source instanceof MenuDiv menuDiv) {
                        System.out.println(menuDiv.getItem().getId());
                    }
                }
        ));

        virtualList.setDataProvider(provider);
        virtualList.setSizeFull();
        virtualList.getStyle().set("border", "1px solid var(--lumo-shade-20pct)");

        // Layout zusammenfügen
        mainLayout.add(grid, virtualList);
        // Setze explizit die Proportionen
        grid.setWidth("90%");
        virtualList.setWidth("10%");
        pageVerticalLayout.add(oben, mainLayout);
        this.add(pageVerticalLayout);
        this.setSizeFull();
    }

    private void updateGrid(int numberOfWeeks) {
        LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        List<Week> updatedWeeks = generateWeeks(startOfWeek, numberOfWeeks);

        // Grid-Daten aktualisieren
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(updatedWeeks);
        dataProvider.refreshAll();
    }

    private List<Week> generateWeeks(LocalDate startOfWeek, Integer numberOfWeeks) {
        List<Week> weeks = new ArrayList<>();
        for (int i = 0; i < numberOfWeeks; i++) {
            LocalDate weekStart = startOfWeek.plusWeeks(i);
            Week week = new Week();
            week.setMon(weekStart);
            week.setTue(weekStart.plusDays(1));
            week.setWed(weekStart.plusDays(2));
            week.setThu(weekStart.plusDays(3));
            week.setFri(weekStart.plusDays(4));
            week.setSat(weekStart.plusDays(5));
            week.setSun(weekStart.plusDays(6));
            weeks.add(week);
        }
        return weeks;
    }

    private void addDayColumn(Grid<Week> grid, String header, Function<Week, LocalDate> dateGetter, DateTimeFormatter formatter) {
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(dateGetter.apply(item), formatter)))
                .setHeader(header);
    }

    private Div createDayCell(LocalDate date, DateTimeFormatter formatter) {
        Div container = new Div();

        NativeLabel dateLabel = new NativeLabel(date.format(formatter));
        Div dropZone = createDropZone();

        container.add(dateLabel, dropZone);
        return container;
    }

    private Div createDropZone() {
        Div dropZone = new Div();
        dropZone.setClassName("offer_target");

        // Initial-Update der Dropzone
        updateDropTargets(dropZone);

        return dropZone;
    }

    private void updateDropTargets(Div dropZone) {
        // Speichere bestehende Wrapper-Items
        List<Component> items = dropZone.getChildren()
                .filter(child -> child.getClassName().equals("item-wrapper"))
                .toList();

        // Lösche alles aus der Dropzone
        dropZone.removeAll();

        // Füge die Before-Linie am Anfang hinzu
        Div beforeTarget = createThinDropTarget(dropZone, 0);
        dropZone.add(beforeTarget);

        // Füge Wrapper und Between-Linien ein
        for (int i = 0; i < items.size(); i++) {
            dropZone.add(items.get(i)); // Wrapper einfügen

            // Between-Linie nach jedem Item
            Div betweenTarget = createThinDropTarget(dropZone, i + 1);
            dropZone.add(betweenTarget);
        }

        // Ersetze die letzte Linie durch eine After-Linie
        dropZone.getChildren()
                .filter(child -> child.getClassName().equals("thin-drop-target"))
                .reduce((first, second) -> second) // Letzte Linie finden
                .ifPresent(last -> last.getElement().setAttribute("class","after-drop-target"));
    }

    private Div createThinDropTarget(Div container, int position) {
        Div thinTarget = new Div();
        thinTarget.setClassName("thin-drop-target");

        // Erstellen des DropTargets
        DropTarget<Div> dropTarget = DropTarget.create(thinTarget);
        dropTarget.addDropListener(event -> event.getDragSourceComponent().ifPresent(source -> {
            if (source instanceof MenuDiv menuDiv) {
                MenuDiv copy = new MenuDiv(menuDiv.getItem());

                // Position des DropTargets ermitteln
                List<Component> children = container.getChildren().toList();
                int dropPosition = children.indexOf(thinTarget); // Index des aktuellen Targets ermitteln

                if (dropPosition != -1) {
                    // Füge das neue Element an der richtigen Stelle hinzu
                    Div wrapper = createWrapper(copy);
                    container.addComponentAtIndex(dropPosition, wrapper);

                    // Aktualisiere die Drop-Zone
                    updateDropTargets(container);
                }
            }
        }));

        return thinTarget;
    }

    private Div createWrapper(Component item) {
        Div wrapper = new Div();
        wrapper.setClassName("item-wrapper");

        // Löschen-Button hinzufügen
        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addClickListener(event -> {
            // Wrapper aus der Dropzone entfernen
            Div parent = (Div) wrapper.getParent().orElse(null);
            if (parent != null) {
                parent.remove(wrapper);
                updateDropTargets(parent); // Drop-Zone nach Löschung aktualisieren
            }
        });

        // Layout für Item und Button
        HorizontalLayout layout = new HorizontalLayout(item, deleteButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER); // Zentriert das Item und den Button
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN); // Schiebt den Button ganz nach rechts
        layout.add(item, deleteButton);

        // Den deleteButton ganz nach rechts verschieben
        layout.setWidthFull();  // Macht das Layout über die gesamte Breite


        wrapper.add(layout);
        return wrapper;
    }


    private static class MenuDiv extends Div {
        private final Menu item;

        public MenuDiv(Menu item) {
            this.item = item;
            Span name = new Span(item.getName());
            this.add(name);
        }

        public Menu getItem() {
            return item;
        }
    }

    // Hinzufügen einer Spalte für die KW vor den Wochentagen
    private void addKWColumn(Grid<Week> grid) {
        grid.addColumn(new ComponentRenderer<>(item -> {
            Div container = new Div();
            // Berechnung der Kalenderwoche (KW)
            int calendarWeek = item.getMon().get(WeekFields.ISO.weekOfWeekBasedYear());
            NativeLabel kwLabel = new NativeLabel("" + calendarWeek);
            container.add(kwLabel);
            return container;
        })).setHeader("KW").setAutoWidth(true).setFrozen(true); // Fixiert die KW-Spalte
    }
}

