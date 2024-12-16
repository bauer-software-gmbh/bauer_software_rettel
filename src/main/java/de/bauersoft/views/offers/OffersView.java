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
import de.bauersoft.data.providers.OffersDataProvider;
import de.bauersoft.services.DayService;
import de.bauersoft.services.MenuService;
import de.bauersoft.services.WeekService;
import de.bauersoft.tools.DatePickerLocaleGerman;
import de.bauersoft.views.MainLayout;
import com.vaadin.flow.component.html.Div;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.DayOfWeek;
import java.time.temporal.WeekFields;
import java.util.*;

@PageTitle("offers")
@Route(value = "offers", layout = MainLayout.class)
@AnonymousAllowed
public class OffersView extends Div {
    private final OffersDataProvider dataProvider;
    private final DayService dayService;
    private final MenuService menuService;
    private final WeekService weekService;
    private LocalDate vonDate, bisDate;
    private final DatePicker filterDate;
    private Button deleteButton;

    public OffersView(MenuService menuService, DayService dayService, WeekService weekService) {
        this.dataProvider = new OffersDataProvider(dayService, menuService);
        this.dayService = dayService;
        this.menuService = menuService;
        this.weekService = weekService;
        this.deleteButton = new Button();

        setClassName("content");
        VerticalLayout pageVerticalLayout = new VerticalLayout();
        pageVerticalLayout.setSizeFull();

        this.filterDate = new DatePicker("Datum:");

        this.filterDate.setWeekNumbersVisible(true);
        this.filterDate.setI18n(new DatePickerLocaleGerman());

        WeekSelector selector1 = new WeekSelector(1, ChronoUnit.WEEKS, "2 Wochen");
        WeekSelector selector2 = new WeekSelector(1, ChronoUnit.MONTHS, "1 Monat");
        WeekSelector selector3 = new WeekSelector(3, ChronoUnit.MONTHS, "3 Monate");
        WeekSelector selector4 = new WeekSelector(1, ChronoUnit.YEARS, "1 Jahr");

        ComboBox<WeekSelector> weekCombobox = new ComboBox<>("Anzeige auswählen:");
        weekCombobox.setItemLabelGenerator(WeekSelector::name);
        weekCombobox.setItems(selector1, selector2, selector3, selector4);
        weekCombobox.setValue(selector1);
        weekCombobox.addValueChangeListener(event -> {
            if(!DayOfWeek.MONDAY.equals(this.filterDate.getValue().getDayOfWeek())){
                this.vonDate =this.filterDate.getValue().minusDays  (this.filterDate.getValue().getDayOfWeek().getValue()-1);
            }
            this.vonDate = this.filterDate.getValue();
            WeekSelector value = event.getValue();
            this.bisDate = this.vonDate.plus( value.amount(),value.unit());
            this.dataProvider.setDateRange(this.vonDate, this.bisDate);
        });

        this.filterDate.addValueChangeListener(event->{
            this.vonDate = event.getValue().minusDays  (event.getValue().getDayOfWeek().getValue()-1);
            var value = weekCombobox.getValue();
            this.bisDate = this.vonDate.plus( value.amount(),value.unit());
            this.dataProvider.setDateRange(this.vonDate, this.bisDate);
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        // Grid-Setup
        Grid<Week> grid = new Grid<>(Week.class, false);
        grid.addColumn(Week::getKw).setHeader("KW");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item.getDayFor(DayOfWeek.MONDAY).getDate(), formatter))).setHeader("Montag");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item.getDayFor(DayOfWeek.TUESDAY).getDate(), formatter))).setHeader("Dienstag");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item.getDayFor(DayOfWeek.WEDNESDAY).getDate(), formatter))).setHeader("Mittwoch");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item.getDayFor(DayOfWeek.THURSDAY).getDate(), formatter))).setHeader("Donnerstag");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item.getDayFor(DayOfWeek.FRIDAY).getDate(), formatter))).setHeader("Freitag");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item.getDayFor(DayOfWeek.SATURDAY).getDate(), formatter))).setHeader("Samstag");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item.getDayFor(DayOfWeek.SUNDAY).getDate(), formatter))).setHeader("Sonntag");

        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setDataProvider(this.dataProvider);
        grid.setSizeFull();

        HorizontalLayout oben  = new HorizontalLayout();
        oben.add(weekCombobox, this.filterDate);
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
        this.filterDate.setValue(LocalDate.now());


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


        Optional<Day> optionalDay = this.dayService.findByDate(date);
        optionalDay.ifPresent(day -> {
            this.deleteButton.setEnabled(day.getDate().isAfter(LocalDate.now()));
            day.getMenus().forEach(menu -> {
                MenuDiv menuDiv = new MenuDiv(menu);
                Div wrapper = createWrapper(day, menuDiv);
                dropZone.add(wrapper);
            });
        });

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

                // Hole das Datum und berechne die Kalenderwoche
                String dateString = thinTarget.getElement().getAttribute("date");
                if (dateString == null || dateString.isEmpty()) {
                    System.out.println("Kein gültiges Datum gefunden.");
                    return;
                }

                LocalDate dropDate = LocalDate.parse(dateString);
                int calendarWeek = dropDate.get(WeekFields.ISO.weekOfWeekBasedYear());
                int year = dropDate.getYear();

                dayService.findByDate(dropDate).ifPresent(day ->
                        {
                            if (dropPosition != -1) {
                                // Füge das neue Element an der richtigen Stelle hinzu
                                Div wrapper = createWrapper(day, copy);
                                container.addComponentAtIndex(dropPosition, wrapper);

                                // Aktualisiere die Drop-Zone
                                updateDropTargets(container);
                            }
                        }
                );
                // Prüfe, ob die Woche existiert
                Optional<Week> optionalWeek = this.weekService.findByCalendarWeekAndYear(calendarWeek, year);
                Week week = optionalWeek.orElseGet(() -> {
                    // Falls nicht vorhanden erstelle eine neue Woche
                    Week newWeek = new Week();
                    newWeek.setKw(calendarWeek);
                    newWeek.setYear(year);
                    this.weekService.saveWeek(newWeek);
                    return newWeek;
                });

                // Prüfe, ob der Tag existiert
                Optional<Day> optionalDay = this.dayService.findByDate(dropDate);
                Day day = optionalDay.orElseGet(() -> {
                    // Falls nicht vorhanden erstelle einen neuen Tag
                    Day newDay = new Day();
                    newDay.setName(dropDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMANY));
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

    private Div createWrapper(Day day, MenuDiv item) {
        Div wrapper = new Div();
        wrapper.setClassName("item-wrapper");

        // Löschen-Button hinzufügen
        this.deleteButton = new Button(VaadinIcon.TRASH.create());
        this.deleteButton.addClickListener(event -> {
            // Wrapper aus der Dropzone entfernen
            Div parent = (Div) wrapper.getParent().orElse(null);
            if (parent != null) {
                parent.remove(wrapper);
                updateDropTargets(parent); // Drop-Zone nach Löschung aktualisieren

                dataProvider.deleteMenuFromDay(day.getId(), item.getItem().getId());
                System.out.println(day.getId() + " : " + item.getItem().getId());

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

    private record WeekSelector(int amount, ChronoUnit unit, String name) {

    }

}

