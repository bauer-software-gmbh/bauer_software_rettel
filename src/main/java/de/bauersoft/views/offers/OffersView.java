package de.bauersoft.views.offers;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.bauersoft.data.entities.Day;
import de.bauersoft.data.entities.Menu;
import de.bauersoft.data.entities.Week;
import de.bauersoft.services.DayService;
import de.bauersoft.services.MenuService;
import de.bauersoft.services.WeekService;
import de.bauersoft.tools.DatePickerLocaleGerman;
import de.bauersoft.tools.DynamicYearComboBox;
import de.bauersoft.views.MainLayout;
import com.vaadin.flow.component.html.Div;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@PageTitle("offers")
@Route(value = "offers", layout = MainLayout.class)
@AnonymousAllowed
public class OffersView extends Div {
    private final ListDataProvider<Week> dataProvider;
    private final DayService dayService;
    private final MenuService menuService;
    private final WeekService weekService;
    private LocalDate vonDate, bisDate;
    private DynamicYearComboBox yearComboBox;
    private DatePicker filterDate;

    public OffersView(MenuService menuService, DayService dayService, WeekService weekService) {
        this.dayService = dayService;
        this.menuService = menuService;
        this.weekService = weekService;

        setClassName("content");
        VerticalLayout pageVerticalLayout = new VerticalLayout();
        pageVerticalLayout.setSizeFull();

        this.filterDate = new DatePicker("Datum:");
        this.filterDate.setValue(LocalDate.now());
        this.filterDate.setWeekNumbersVisible(true);
        this.filterDate.setI18n(new DatePickerLocaleGerman());
        this.filterDate.addValueChangeListener(event-> {
            TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
            int weekNumber = event.getValue().get(woy);
            System.out.println("week number:" + weekNumber);
        });

        this.yearComboBox = new DynamicYearComboBox("Jahr wählen:");
        this.yearComboBox.checkAndUpdateYears();
        this.yearComboBox.setEnabled(false);
        this.yearComboBox.addValueChangeListener(event -> {

        });

        this.vonDate = this.filterDate.getValue();
        this.bisDate = this.vonDate.plusWeeks(1);

        ComboBox<String> weekCombobox = new ComboBox<>("Anzeige auswählen:");
        weekCombobox.setItems(Arrays.asList("2 Wochen", "1 Monat", "3 Monate", "1 Jahr"));
        weekCombobox.setValue("2 Wochen");
        weekCombobox.addValueChangeListener(event -> {
            this.vonDate = this.filterDate.getValue();

            switch (event.getValue()) {
                case "2 Wochen":
                    this.bisDate = this.vonDate.plusWeeks(1); // Exakt 2 Wochen
                    this.yearComboBox.setEnabled(false);
                    this.filterDate.setEnabled(true);
                    break;
                case "1 Monat":
                    // Erster Tag des Monats
                    this.vonDate = this.vonDate.withDayOfMonth(1);
                    // Letzter Tag des Monats
                    this.bisDate = this.vonDate.plusMonths(1).minusDays(1);

                    this.yearComboBox.setEnabled(false);
                    this.filterDate.setEnabled(true);

                    // Wähle nur den Teil des Monats, der innerhalb der angezeigten Zeitspanne liegt
                    if (this.filterDate.getValue().isBefore(this.vonDate)) {
                        this.vonDate = vonDate; // Wenn das Startdatum vor dem aktuellen Monat liegt, den Start auf den ersten Tag des Monats setzen
                    } else {
                        this.vonDate = vonDate; // Belassen
                    }
                    break;
                case "3 Monate":
                    // Erster Tag des Monats
                    this.vonDate = this.vonDate.withDayOfMonth(1);
                    // Letzter Tag des Monats nach 3 Monaten
                    this.bisDate = this.vonDate.plusMonths(3).minusDays(1);
                    this.yearComboBox.setEnabled(false);
                    this.filterDate.setEnabled(true);
                    break;
                case "1 Jahr":
                default:
                    // Erster Tag des Jahres
                    this.vonDate = this.vonDate.withMonth(1).withDayOfMonth(1);
                    // Letzter Tag des Jahres
                    this.bisDate = this.vonDate.plusYears(1).minusDays(1);
                    this.yearComboBox.setEnabled(true);
                    this.filterDate.setEnabled(false);
                    break;
            }

            updateGrid(this.vonDate, this.bisDate);  // Zeige nur den gültigen Zeitraum
        });

        // Startdatum und initiale Daten
        LocalDate startOfWeek = this.vonDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        List<Week> weeks = generateWeeks(startOfWeek, this.bisDate);
        this.dataProvider = new ListDataProvider<>(weeks);

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
        grid.setDataProvider(this.dataProvider);
        grid.setSizeFull();

        HorizontalLayout oben  = new HorizontalLayout();
        oben.add(weekCombobox, this.yearComboBox, this.filterDate);
        oben.setAlignItems(FlexComponent.Alignment.CENTER); // Alles zentrieren

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();

        VirtualList<Menu> virtualList = new VirtualList<>();
        virtualList.setRenderer(new ComponentRenderer<Div,Menu>(component->{
            MenuDiv container = new MenuDiv(component);
            DragSource.create(container);
            return container;
        }));
        List<Menu> list = menuService.findAll();
        ListDataProvider<Menu> provider = new ListDataProvider<>(list);

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

    private void updateGrid(LocalDate startDate, LocalDate endDate) {
        List<Week> updatedWeeks = generateWeeks(
                startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                endDate
        );

        // Grid-Daten aktualisieren
        this.dataProvider.getItems().clear();
        this.dataProvider.getItems().addAll(updatedWeeks);
        this.dataProvider.refreshAll();
    }

    private List<Week> generateWeeks(LocalDate startDate, LocalDate endDate) {
        List<Week> weeks = new ArrayList<>();

        long weeksBetween = ChronoUnit.WEEKS.between(startDate, endDate) + 1; // Ensure inclusive range

        for (int i = 0; i < weeksBetween; i++) {
            LocalDate weekStart = startDate.plusWeeks(i);
            if (!weekStart.isBefore(endDate)) break; // Avoid adding weeks beyond the endDate

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
        Div dropZone = createDropZone(date);

        container.add(dateLabel, dropZone);
        return container;
    }

    private Div createDropZone(LocalDate date) {
        Div dropZone = new Div();
        dropZone.setClassName("offer_target");
        dropZone.getElement().setAttribute("date", date.toString());  // Setze das Datum als Attribut

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
        thinTarget.getElement().setAttribute("date", container.getElement().getAttribute("date"));

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

                // Hole das Datum und berechne die Kalenderwoche
                String dateString = thinTarget.getElement().getAttribute("date");
                if (dateString == null || dateString.isEmpty()) {
                    System.out.println("Kein gültiges Datum gefunden.");
                    return;
                }

                LocalDate dropDate = LocalDate.parse(dateString);
                int calendarWeek = dropDate.get(WeekFields.ISO.weekOfWeekBasedYear());
                int year = dropDate.getYear();

                // Prüfe, ob die Woche existiert
                Optional<Week> optionalWeek = this.weekService.findByCalendarWeekAndYear(calendarWeek, year);
                Week week = optionalWeek.orElseGet(() -> {
                    // Falls nicht vorhanden, erstelle eine neue Woche
                    Week newWeek = new Week();
                    newWeek.setKw(calendarWeek);
                    newWeek.setYear(year);
                    this.weekService.saveWeek(newWeek);
                    return newWeek;
                });

                // Prüfe, ob der Tag existiert
                Optional<Day> optionalDay = this.dayService.findByDate(dropDate);
                Day day = optionalDay.orElseGet(() -> {
                    // Falls nicht vorhanden, erstelle einen neuen Tag
                    Day newDay = new Day();
                    newDay.setDate(dropDate);
                    newDay.setWeek(week);
                    this.dayService.saveDay(newDay);
                    return newDay;
                });

                // Füge das Menü zum Tag hinzu
                this.menuService.addMenuToDay(day, copy.getItem());

                // Debugging
                Menu menu = copy.getItem();
                System.out.println("Menü hinzugefügt:");
                System.out.println("ID: " + menu.getId());
                System.out.println("Name: " + menu.getName());
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
            return this.item;
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

