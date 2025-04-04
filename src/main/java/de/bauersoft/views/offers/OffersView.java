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
import de.bauersoft.data.entities.field.DefaultField;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.flesh.DefaultFlesh;
import de.bauersoft.data.entities.flesh.Flesh;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.offer.Offer;
import de.bauersoft.data.entities.pattern.DefaultPattern;
import de.bauersoft.data.entities.role.Role;
import de.bauersoft.data.entities.variant.Variant;
import de.bauersoft.data.providers.OffersDataProvider;
import de.bauersoft.services.FieldService;
import de.bauersoft.services.MenuService;
import de.bauersoft.services.offer.OfferService;
import de.bauersoft.services.offer.Week;
import de.bauersoft.tools.DatePickerLocaleGerman;
import de.bauersoft.views.MainLayout;
import com.vaadin.flow.component.html.Div;
import de.bauersoft.views.menue.CreateMenuPdf;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Stream;

@PageTitle("Menü Planung")
@Route(value = "offers", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "KITCHEN_ADMIN", "OFFICE_ADMIN"})
public class OffersView extends Div
{
    private final OffersDataProvider dataProvider;
    private final OfferService offerService;
    private LocalDate vonDate, bisDate;
    private final DatePicker filterDate;
    private Button generatePDF;
    private ComboBox<Field> fieldComboBox;
    private final Logger logger = LoggerFactory.getLogger(OffersView.class);

    public OffersView(MenuService menuService, FieldService fieldService, OfferService offerService)
    {
        this.offerService = offerService;
        this.dataProvider = new OffersDataProvider(offerService, menuService);
        this.generatePDF = new Button("Generate PDF"); // Initialisiere den Button
        generatePDF.setVisible(true); // Standardmäßig unsichtbar
        generatePDF.addClickListener(event ->
                createMenuPdf()); // Button-Click-Listener hinzufügen

        setClassName("content");
        VerticalLayout pageVerticalLayout = new VerticalLayout();
        pageVerticalLayout.setSizeFull();

        this.filterDate = new DatePicker("Datum:");
        this.filterDate.setWeekNumbersVisible(true);
        this.filterDate.setI18n(new DatePickerLocaleGerman());

        // Neue ComboBox für Field hinzufügen
        fieldComboBox = new ComboBox<>("Einrichtungsart:");
        fieldComboBox.setItemLabelGenerator(Field::getName); // Anzeige in der ComboBox
        fieldComboBox.setItems(fieldService.findAll());


        fieldComboBox.addValueChangeListener(event ->
        {
            Field selectedField = event.getValue();
            if(selectedField != null)
            {
//                boolean shouldShowButton = Stream.of(DefaultField.GRUNDSCHULE, DefaultField.KINDERTAGESSTAETTE, DefaultField.KINDERGARTEN).anyMatch(defaultField -> defaultField.equalsDefault(selectedField));
//
//                generatePDF.setVisible(shouldShowButton);
                // Setze die Field-ID und aktualisiere die Daten
                dataProvider.setFieldId(selectedField.getId());
                // Benachrichtige das Grid, dass es sich aktualisieren soll
                dataProvider.refreshAll();
            }else
            {
                generatePDF.setVisible(false); // Verstecke den Button, wenn nichts ausgewählt ist
            }
        });
        fieldComboBox.setValue(fieldService.findAll().getFirst()); // Setze Standardwert


        ComboBox<WeekSelector> weekCombobox = getWeekSelectorComboBox();

        this.filterDate.addValueChangeListener(event ->
        {
            if (event.getValue() == null) return;

            this.vonDate = event.getValue().minusDays(event.getValue().getDayOfWeek().getValue() - 1);
            var value = weekCombobox.getValue();
            this.bisDate = this.vonDate.plus(value.amount(), value.unit());

            logger.info("DatePicker changed: {} - {}", vonDate, bisDate);

            this.dataProvider.setDateRange(this.vonDate, this.bisDate);
            this.dataProvider.refreshAll();
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        Grid<LocalDate> grid = new Grid<>(LocalDate.class, false);
        grid.addColumn(Week::getKw).setHeader("KW").setWidth("50px").setFlexGrow(0).setFrozen(true);
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(Week.getDate(DayOfWeek.MONDAY, item), formatter))).setHeader("Montag");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(Week.getDate(DayOfWeek.TUESDAY, item), formatter))).setHeader("Dienstag");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(Week.getDate(DayOfWeek.WEDNESDAY, item), formatter))).setHeader("Mittwoch");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(Week.getDate(DayOfWeek.THURSDAY, item), formatter))).setHeader("Donnerstag");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(Week.getDate(DayOfWeek.FRIDAY, item), formatter))).setHeader("Freitag");
        //grid.addColumn(new ComponentRenderer<>(item -> createDayCell(Week.getDate(DayOfWeek.SATURDAY, item), formatter))).setHeader("Samstag");
        //grid.addColumn(new ComponentRenderer<>(item -> createDayCell(Week.getDate(DayOfWeek.SUNDAY, item), formatter))).setHeader("Sonntag");

        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setDataProvider(this.dataProvider);
        grid.setSizeFull();

        HorizontalLayout oben = new HorizontalLayout();
        oben.add(fieldComboBox, weekCombobox, this.filterDate, generatePDF); // Button hinzugefügt
        oben.setAlignItems(FlexComponent.Alignment.CENTER); // Alles zentrieren

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();

        VirtualList<Menu> virtualList = new VirtualList<>();
        virtualList.setRenderer(new ComponentRenderer<Div, Menu>(component ->
        {
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
        grid.setWidth("90%");
        virtualList.setWidth("10%");
        pageVerticalLayout.add(oben, mainLayout);
        this.add(pageVerticalLayout);
        this.setSizeFull();
        this.filterDate.setValue(LocalDate.now());
    }

    private void createMenuPdf()
    {
        LocalDate startDate = filterDate.getValue().with(DayOfWeek.MONDAY);
        LocalDate endDate = startDate.plusWeeks(3).with(DayOfWeek.FRIDAY);

        List<Map<String, Object>> menuList = new ArrayList<>();
        Field field = fieldComboBox.getValue();

        System.out.println(new StringBuilder().repeat("-", 500).append("Oben: ").append(field != null ? field.getName() : "Kein Feld ausgewählt"));

        for(LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1))
        {
            if(date.getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY))
            {
                continue;
            }

            Optional<Offer> offer = offerService.findByLocalDateAndField(date, field);
            if(offer.isPresent())
            {
                Offer currentOffer = offer.get();
                if(currentOffer.getMenus() != null && !currentOffer.getMenus().isEmpty())
                {
                    Optional<Menu> optionalMenu = currentOffer.getMenus().stream().findFirst();
                    LocalDate finalDate = date;
                    optionalMenu.ifPresent(menu ->
                    {
                        // Fleisch-Typ bestimmen
                        String fleshType = "Vegetarisch"; // Standardwert
                        Flesh flesh = menu.getFlesh();
                        if(flesh != null)
                        {
                            if(DefaultFlesh.BEEF.equalsDefault(flesh))
                            {
                                fleshType = "Rindfleisch";
                            }else if(DefaultFlesh.CHICKEN.equalsDefault(flesh))
                            {
                                fleshType = "Hähnchen";
                            }else if(DefaultFlesh.FISH.equalsDefault(flesh))
                            {
                                fleshType = "Fisch";
                            }
                        }

                        // Varianten extrahieren
                        String normalDescription = "kein Menu";
                        String vegetarianDescription = "Keine Beschreibung";

                        Optional<Variant> normalVariant = menu.getVariants().stream()
                                .filter(variant -> DefaultPattern.DEFAULT.equalsDefault(variant.getPattern()))
                                .findFirst();

                        if(normalVariant.isPresent())
                            normalDescription = normalVariant.get().getDescription();

                        Optional<Variant> vegetarianVariant = menu.getVariants().stream()
                                .filter(variant -> DefaultPattern.VEGETARIAN.equalsDefault(variant.getPattern()))
                                .findFirst();

                        if(vegetarianVariant.isPresent())
                            vegetarianDescription = vegetarianVariant.get().getDescription();

                        // Menü-Daten speichern
                        Map<String, Object> menuEntry = new HashMap<>();
                        menuEntry.put("day", finalDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                        menuEntry.put("menu", normalDescription);
                        menuEntry.put("type", fleshType);
                        menuEntry.put("alternative", vegetarianDescription);
                        menuList.add(menuEntry);
                    });
                    continue;
                }

            }

            Map<String, Object> emptyDayEntry = new HashMap<>();
            emptyDayEntry.put("day", date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            emptyDayEntry.put("menu", "kein Menu");
            emptyDayEntry.put("type", "");
            emptyDayEntry.put("alternative", "Keine Beschreibung");
            menuList.add(emptyDayEntry);
        }

        // PDF-Erstellung mit den gesammelten Daten
        System.out.println(new StringBuilder().repeat("-", 500).append("Unten: ").append(field.getName()));
        if (field != null) {
            CreateMenuPdf.generatePdf(menuList, field.getName());
        } else {
            System.err.println("Fehler: Kein Feld ausgewählt. PDF-Generierung abgebrochen.");
        }
    }


    private ComboBox<WeekSelector> getWeekSelectorComboBox()
    {
        ComboBox<WeekSelector> weekCombobox = getSelectorComboBox();

        // Erstmaligen Wert setzen und sicherstellen, dass die Methode sich aktualisiert
        weekCombobox.setValue(new WeekSelector(1, ChronoUnit.WEEKS, "2 Wochen")); // Standard-Wert setzen

        weekCombobox.addValueChangeListener(event ->
        {
            if (this.filterDate.getValue() == null)
            {
                return; // Falls kein Datum gesetzt ist, keine Änderung
            }

            this.vonDate = this.filterDate.getValue();

            if(!DayOfWeek.MONDAY.equals(this.filterDate.getValue().getDayOfWeek()))
            {
                this.vonDate = this.filterDate.getValue().minusDays(this.filterDate.getValue().getDayOfWeek().getValue() - 1);
            }

            WeekSelector value = event.getValue();
            this.bisDate = this.vonDate.plus(value.amount(), value.unit());

            this.dataProvider.setDateRange(this.vonDate, this.bisDate);
            this.dataProvider.refreshAll();
        });
        return weekCombobox;
    }

    private static ComboBox<WeekSelector> getSelectorComboBox()
    {
        WeekSelector selector1 = new WeekSelector(1, ChronoUnit.WEEKS, "2 Wochen");
        WeekSelector selector2 = new WeekSelector(1, ChronoUnit.MONTHS, "1 Monat");
        WeekSelector selector3 = new WeekSelector(3, ChronoUnit.MONTHS, "3 Monate");
        WeekSelector selector4 = new WeekSelector(1, ChronoUnit.YEARS, "1 Jahr");

        ComboBox<WeekSelector> weekCombobox = new ComboBox<>("Anzeige auswählen:");
        weekCombobox.setItemLabelGenerator(WeekSelector::name);
        weekCombobox.setItems(selector1, selector2, selector3, selector4);
        weekCombobox.setValue(selector1);
        return weekCombobox;
    }

    private Div createDayCell(LocalDate date, DateTimeFormatter formatter)
    {
        Div container = new Div();

        NativeLabel dateLabel = new NativeLabel(date.format(formatter));
        Div dropZone = createDropZone(date);

        container.add(dateLabel, dropZone);
        return container;
    }

    private Div createDropZone(LocalDate date)
    {
        Div dropZone = new Div();
        dropZone.setClassName("offer_target");
        dropZone.getElement().setAttribute("date", date.toString());

        Optional<Offer> optionalOffer = this.offerService.getByLocalDateAndField(date, fieldComboBox.getValue());

        Offer offer = optionalOffer.orElseGet(() ->
        {
            Offer newOffer = new Offer();
            newOffer.setLocalDate(date);
            newOffer.setField(fieldComboBox.getValue());
            newOffer.setMenus(new HashSet<>());

            return offerService.update(newOffer); // Speichern und zurückgeben
        });

        offer.getMenus().forEach(menu ->
        {
            MenuDiv menuDiv = new MenuDiv(menu);
            Div wrapper = createWrapper(offer, menuDiv);
            dropZone.add(wrapper);
        });

        updateDropTargets(dropZone);
        return dropZone;
    }


    private void updateDropTargets(Div dropZone)
    {
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
        for(int i = 0; i < items.size(); i++)
        {
            dropZone.add(items.get(i)); // Wrapper einfügen

            // Between-Linie nach jedem Item
            Div betweenTarget = createThinDropTarget(dropZone, i + 1);
            dropZone.add(betweenTarget);
        }

        // Ersetze die letzte Linie durch eine After-Linie
        dropZone.getChildren()
                .filter(child -> child.getClassName().equals("thin-drop-target"))
                .reduce((first, second) -> second) // Letzte Linie finden
                .ifPresent(last -> last.getElement().setAttribute("class", "after-drop-target"));
    }

    private Div createThinDropTarget(Div dropZone, int position)
    {
        Div thinTarget = new Div();
        thinTarget.setClassName("thin-drop-target");
        thinTarget.getElement().setAttribute("date", dropZone.getElement().getAttribute("date"));
        thinTarget.setVisible(true);

        LocalDate localDate = LocalDate.parse(dropZone.getElement().getAttribute("date"));

        // Prüfen, ob bereits ein Menü für diesen Tag existiert
        Optional<Offer> optionalOffer = this.offerService.getByLocalDateAndField(localDate, fieldComboBox.getValue());
        if(optionalOffer.isPresent() && !optionalOffer.get().getMenus().isEmpty())
        {
            thinTarget.setVisible(false);
        }

        if(localDate.isBefore(LocalDate.now()))
        {
            thinTarget.setVisible(false);
        }

        DropTarget<Div> dropTarget = DropTarget.create(thinTarget);
        dropTarget.addDropListener(event -> event.getDragSourceComponent().ifPresent(source ->
        {
            if(source instanceof MenuDiv menuDiv)
            {
                MenuDiv copy = new MenuDiv(menuDiv.getItem());

                List<Component> children = dropZone.getChildren().toList();
                int dropPosition = children.indexOf(thinTarget);

                String dateString = thinTarget.getElement().getAttribute("date");
                if(dateString == null || dateString.isEmpty())
                {
                    System.out.println("Kein gültiges Datum gefunden.");
                    return;
                }

                LocalDate dropDate = LocalDate.parse(dateString);
                Field selectedField = fieldComboBox.getValue(); // Field aus ComboBox holen

                // `Offer` für das Datum und Field holen oder neu erstellen
                Offer offer = offerService.getByLocalDateAndField(dropDate, selectedField)
                        .orElseGet(() ->
                        {
                            Offer newOffer = new Offer();
                            newOffer.setLocalDate(dropDate);
                            newOffer.setField(selectedField);
                            newOffer.setMenus(new HashSet<>()); // Neues Offer hat noch keine Menüs

                            return offerService.update(newOffer); // Speichern und zurückgeben
                        });

                // Prüfen, ob das Menü schon vorhanden ist
                if(offer.getMenus().stream().anyMatch(menu -> Objects.equals(menu.getId(), copy.getItem().getId())))
                {
                    return; // Falls das Menü schon existiert, nichts tun
                }

                // Menü zum Offer hinzufügen
                offer.getMenus().add(copy.getItem());
                offerService.update(offer); // Speichern


                if(dropPosition != -1)
                {
                    Div wrapper = createWrapper(offer, copy);
                    dropZone.addComponentAtIndex(dropPosition, wrapper);

                    // Verhindert, dass weitere Menüs hinzugefügt werden können
                    thinTarget.setVisible(false);

                    updateDropTargets(dropZone);
                }
            }
        }));

        return thinTarget;
    }


    private Div createWrapper(Offer offer, MenuDiv item)
    {
        Div wrapper = new Div();
        wrapper.setClassName("item-wrapper");
        boolean isDeletable = offer.getLocalDate().isEqual(LocalDate.now()) || offer.getLocalDate().isAfter(LocalDate.now());

        Button deleteButton = new Button(VaadinIcon.TRASH.create());

        deleteButton.setEnabled(isDeletable);

        deleteButton.addClickListener(event ->
        {
            Div parent = (Div) wrapper.getParent().orElse(null);
            if(parent != null)
            {
                parent.remove(wrapper);
                updateDropTargets(parent);

                // Menü aus Offer entfernen
                offerService.removeMenuFromOffer(offer.getId(), item.getItem().getId());
                logger.info("Menü entfernt: {} : {}", offer.getId(), item.getItem().getId());
                dataProvider.refreshAll();
            }
        });

        HorizontalLayout layout = new HorizontalLayout(item, deleteButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        layout.setWidthFull();

        wrapper.add(layout);
        return wrapper;
    }

    @Getter
    private static class MenuDiv extends Div
    {
        private final Menu item;

        public MenuDiv(Menu item)
        {
            this.item = item;
            Span name = new Span(item.getName());
            name.getElement().setAttribute("title", item.getName()); // Tooltip anzeigen
            name.getElement().getStyle()
                    .set("white-space", "nowrap")
                    .set("overflow", "hidden")
                    .set("text-overflow", "ellipsis")
                    .set("max-width", "80px") // oder eine andere Breite setzen
                    .set("display", "inline-block");
            this.add(name);
        }
    }

    private record WeekSelector(int amount, ChronoUnit unit, String name)
    {

    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        logger.warn("deleteButton.setEnabled({}) aufgerufen! Stacktrace:", enabled);
        Thread.dumpStack();
    }
}

