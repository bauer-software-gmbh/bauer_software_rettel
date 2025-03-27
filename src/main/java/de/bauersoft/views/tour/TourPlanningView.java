package de.bauersoft.views.tour;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.data.entities.tourPlanning.tour.Tour;
import de.bauersoft.data.entities.tourPlanning.tour.TourEntry;
import de.bauersoft.data.entities.tourPlanning.tour.TourInformation;
import de.bauersoft.services.offer.Week;
import de.bauersoft.services.tourPlanning.TourEntryService;
import de.bauersoft.services.tourPlanning.TourInformationService;
import de.bauersoft.services.tourPlanning.TourService;
import de.bauersoft.tools.DatePickerLocaleGerman;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Touren Planung")
@Route(value = "tour-planning", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "LOGISTICS"})
public class TourPlanningView extends Div {

    private final TourService tourService;
    private final TourEntryService tourEntryService;
    private final TourInformationService tourInformationService;
    private final Logger logger = LoggerFactory.getLogger(TourPlanningView.class);
    private final Grid<LocalDate> grid;
    private final ComboBox<WeekSelector> weekSelectorBox;
    private final DatePicker filterDate;

    public TourPlanningView(TourService tourService, TourEntryService tourEntryService, TourInformationService tourInformationService) {
        this.tourService = tourService;
        this.tourEntryService = tourEntryService;
        this.tourInformationService = tourInformationService;

        setClassName("content");
        VerticalLayout pageVerticalLayout = new VerticalLayout();
        pageVerticalLayout.setSizeFull();

        this.filterDate = new DatePicker("Datum:");
        this.filterDate.setI18n(new DatePickerLocaleGerman());
        this.filterDate.setWeekNumbersVisible(true);
        this.filterDate.setValue(LocalDate.now());

        this.weekSelectorBox = getWeekSelectorComboBox();

        this.filterDate.addValueChangeListener(event -> updateDateRange(weekSelectorBox.getValue()));
        this.weekSelectorBox.addValueChangeListener(event -> updateDateRange(event.getValue()));

        HorizontalLayout filterLayout = new HorizontalLayout(filterDate, weekSelectorBox);
        filterLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        this.grid = buildWeekGrid();
        VirtualList<Tour> tourList = buildTourList();

        HorizontalLayout mainLayout = new HorizontalLayout(grid, tourList);
        mainLayout.setSizeFull();
        grid.setWidth("90%");
        tourList.setWidth("10%");

        pageVerticalLayout.add(filterLayout, mainLayout);
        this.add(pageVerticalLayout);
        this.setSizeFull();

        updateDateRange(getSelectedWeekSelector());
    }

    private void updateDateRange(WeekSelector selector) {
        if (filterDate.getValue() == null) return;

        LocalDate vonDate = filterDate.getValue().with(DayOfWeek.MONDAY);
        LocalDate bisDate = vonDate.plus(selector.amount(), selector.unit());

        logger.info("Zeitraum gesetzt: {} bis {}", vonDate, bisDate);

        List<LocalDate> tage = new ArrayList<>();
        LocalDate cursor = vonDate;
        while (!cursor.isAfter(bisDate)) {
            tage.add(cursor);
            cursor = cursor.plusWeeks(1);
        }

        grid.setItems(tage);
    }

    private Grid<LocalDate> buildWeekGrid() {
        Grid<LocalDate> grid = new Grid<>(LocalDate.class, false);
        grid.addColumn(Week::getKw).setHeader("KW");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item, DayOfWeek.MONDAY, formatter))).setHeader("Montag");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item, DayOfWeek.TUESDAY, formatter))).setHeader("Dienstag");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item, DayOfWeek.WEDNESDAY, formatter))).setHeader("Mittwoch");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item, DayOfWeek.THURSDAY, formatter))).setHeader("Donnerstag");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item, DayOfWeek.FRIDAY, formatter))).setHeader("Freitag");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item, DayOfWeek.SATURDAY, formatter))).setHeader("Samstag");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item, DayOfWeek.SUNDAY, formatter))).setHeader("Sonntag");

        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setSizeFull();
        return grid;
    }

    private Div createDayCell(LocalDate base, DayOfWeek day, DateTimeFormatter formatter) {
        LocalDate date = base.with(day);
        Div container = new Div();
        container.getStyle().set("padding", "4px");

        NativeLabel dateLabel = new NativeLabel(date.format(formatter));
        container.add(dateLabel);

        Div dropZone = new Div();
        dropZone.setClassName("tour-dropzone");
        dropZone.getStyle()
                .set("border", "1px dashed #ccc")
                .set("min-height", "50px")
                .set("padding", "4px")
                .set("margin-top", "4px");

        // TourEntries für dieses Datum laden
        List<TourEntry> entries = tourEntryService.getByDate(date);
        for (TourEntry entry : entries) {
            Div tourLabel = createTourLabel(entry);
            dropZone.add(tourLabel);
        }

        DropTarget<Div> dropTarget = DropTarget.create(dropZone);
        dropTarget.addDropListener(event -> event.getDragSourceComponent().ifPresent(source -> {
            if (source instanceof TourDiv tourDiv) {
                Tour tour = tourDiv.getTour();

                if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    Notification.show("Touren können nur an Werktagen geplant werden.", 3000, Notification.Position.MIDDLE);
                    return;
                }

                logger.info("🛬 Tour '{}' auf {} gedroppt", tour.getName(), date);
                tourEntryService.saveEntry(tour, date);
                updateDateRange(getSelectedWeekSelector());
            }
        }));



        container.add(dropZone);
        return container;
    }

    private Div createTourLabel(TourEntry entry) {
        Div wrapper = new Div();
        wrapper.getStyle()
                .set("background-color", "#f9f9f9")
                .set("border", "1px solid #ccc")
                .set("padding", "4px")
                .set("margin-bottom", "4px")
                .set("display", "flex")
                .set("justify-content", "space-between")
                .set("align-items", "center");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.setMargin(false);

        Span name = new Span(entry.getTour().getName());
        name.getStyle().set("font-weight", "bold");

        Span infoText = new Span(entry.getInfo() != null
                ? entry.getInfo().getTimeWindow()
                : "(kein Zeitfenster)");
        infoText.getStyle().set("font-size", "smaller").set("color", "#666");

        content.add(name, infoText);
        content.addClickListener(e -> {
            if (e.getClickCount() == 2) {
                openInfoDialog(entry);
            }
        });

        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.getElement().setProperty("title", "Löschen");
        deleteButton.addClickListener(click -> {
            tourEntryService.deleteEntry(entry.getId());
            updateDateRange(getSelectedWeekSelector()); // bleibt auf ausgewähltem Zeitraum
        });

        HorizontalLayout layout = new HorizontalLayout(content, deleteButton);
        layout.setWidthFull();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        wrapper.add(layout);

        wrapper.getElement().setAttribute("title", buildTooltipText(entry.getInfo()));

        return wrapper;
    }

    private void openInfoDialog(TourEntry entry) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Tour-Information bearbeiten");

        TextField timeField = new TextField("Zeitfenster");
        timeField.setWidthFull();
        TextArea notesArea = new TextArea("Notiz");
        notesArea.setWidthFull();
        notesArea.setHeightFull();

        if (entry.getInfo() != null) {
            timeField.setValue(entry.getInfo().getTimeWindow() != null ? entry.getInfo().getTimeWindow() : "");
            notesArea.setValue(entry.getInfo().getNotes() != null ? entry.getInfo().getNotes() : "");
        }

        // 💡 Kopier-Dialog
        Button copyButton = new Button("Eintrag kopieren", e -> {
            Dialog copyDialog = new Dialog();
            copyDialog.setHeaderTitle("Tour kopieren");

            DatePicker startDate = new DatePicker("Von");
            startDate.setValue(entry.getDate().plusDays(1));
            startDate.setWidthFull();

            DatePicker endDate = new DatePicker("Bis");
            endDate.setValue(startDate.getValue());
            endDate.setWidthFull();

            // Synchronisiere endDate mit startDate
            startDate.addValueChangeListener(ev -> {
                if (ev.getValue() != null) {
                    endDate.setValue(ev.getValue());
                }
            });

            Button saveCopyBtn = new Button("Speichern", saveEvent -> {
                LocalDate from = startDate.getValue();
                LocalDate to = endDate.getValue();

                if (from == null || to == null) {
                    Notification.show("Bitte beide Datumsfelder ausfüllen.", 3000, Notification.Position.MIDDLE);
                    return;
                }

                if (from.isAfter(to)) {
                    Notification.show("Das 'Von'-Datum darf nicht nach dem 'Bis'-Datum liegen.", 3000, Notification.Position.MIDDLE);
                    return;
                }

                for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
                    DayOfWeek dow = date.getDayOfWeek();
                    if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) continue;

                    TourEntry newEntry = new TourEntry();
                    newEntry.setDate(date);
                    newEntry.setTour(entry.getTour());
                    tourEntryService.save(newEntry);
                }

                copyDialog.close();
                dialog.close();
                updateDateRange(getSelectedWeekSelector());
            });

            Button cancelCopyBtn = new Button("Abbrechen", ev -> copyDialog.close());
            cancelCopyBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

            HorizontalLayout footer = new HorizontalLayout(saveCopyBtn, cancelCopyBtn);
            VerticalLayout layout = new VerticalLayout(startDate, endDate);
            copyDialog.add(layout, footer);
            copyDialog.open();
        });
        copyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        copyButton.getStyle()
                .set("margin-top", "0.5rem")
                .set("align-self", "start")
                .set("background-color", "#E3F2FD")
                .set("color", "#0D47A1")
                .set("border-radius", "8px")
                .set("box-shadow", "var(--lumo-box-shadow-s)")
                .set("font-weight", "500");


        // Standard Speichern
        Button saveButton = new Button("Speichern", e -> {
            String time = timeField.getValue();
            String notes = notesArea.getValue();

            TourInformation info = entry.getInfo();
            if (info == null) {
                info = new TourInformation();
            }

            info.setTimeWindow(time);
            info.setNotes(notes);

            TourInformation savedInfo = tourInformationService.save(info);
            entry.setInfo(savedInfo);
            tourEntryService.update(entry);

            dialog.close();
            updateDateRange(getSelectedWeekSelector());
        });
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.setMinWidth("150px");
        saveButton.setMaxWidth("180px");

        Button cancelButton = new Button("Abbrechen", e -> dialog.close());
        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.setMinWidth("150px");
        cancelButton.setMaxWidth("180px");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        dialog.getFooter().add(saveButton, cancelButton);

        VerticalLayout layout = new VerticalLayout(timeField, notesArea, copyButton);
        layout.setSizeFull();
        dialog.add(layout);

        dialog.open();
    }


    private VirtualList<Tour> buildTourList() {
        List<Tour> tours = tourService.findAll();
        VirtualList<Tour> tourList = new VirtualList<>();
        tourList.setDataProvider(new ListDataProvider<>(tours));

        tourList.setRenderer(new ComponentRenderer<>(tour -> {
            TourDiv div = new TourDiv(tour);
            DragSource.create(div);
            return div;
        }));


        tourList.setSizeFull();
        tourList.getStyle().set("border", "1px solid var(--lumo-shade-20pct)");
        return tourList;
    }

    private static ComboBox<WeekSelector> getWeekSelectorComboBox() {
        WeekSelector selector1 = new WeekSelector(1, ChronoUnit.WEEKS, "2 Wochen");
        WeekSelector selector2 = new WeekSelector(1, ChronoUnit.MONTHS, "1 Monat");

        ComboBox<WeekSelector> box = new ComboBox<>("Zeitraum:");
        box.setItems(selector1, selector2);
        box.setItemLabelGenerator(WeekSelector::name);
        box.setValue(selector1);
        return box;
    }

    private WeekSelector getSelectedWeekSelector() {
        return weekSelectorBox.getValue();
    }

    private String buildTooltipText(TourInformation info) {
        if (info == null) return "";
        StringBuilder sb = new StringBuilder();
        if (info.getTimeWindow() != null && !info.getTimeWindow().isBlank()) {
            sb.append("Zeitfenster: ").append(info.getTimeWindow()).append("\n");
        }
        if (info.getNotes() != null && !info.getNotes().isBlank()) {
            sb.append("Notiz: ").append(info.getNotes());
        }
        return sb.toString().trim();
    }

    private record WeekSelector(int amount, ChronoUnit unit, String name) {}

    @Getter
    private static class TourDiv extends Div {
        private final Tour tour;

        public TourDiv(Tour tour) {
            this.tour = tour;
            setText(tour.getName());
            getStyle().set("padding", "4px").set("border", "1px solid #ccc").set("margin", "2px");
        }
    }
}
