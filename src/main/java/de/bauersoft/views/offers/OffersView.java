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
import de.bauersoft.data.entities.variant.Variant;
import de.bauersoft.data.repositories.flesh.FleshRepository;
import de.bauersoft.data.repositories.menu.MenuRepository;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.DayOfWeek;
import java.util.*;

@PageTitle("Menü Planung")
@Route(value = "offers", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class OffersView extends Div
{
    private final OffersDataProvider dataProvider;
    private final MenuService menuService;
    private final OfferService offerService;
    private final MenuRepository menuRepository;
    private final FleshRepository fleshRepository;
    private LocalDate vonDate, bisDate;
    private final DatePicker filterDate;
    private Button deleteButton, generatePDF;
    private ComboBox<Field> fieldComboBox;

    public OffersView(MenuService menuService, FieldService fieldService, OfferService offerService, MenuRepository menuRepository, FleshRepository fleshRepository)
    {
        this.offerService = offerService;
        this.fleshRepository = fleshRepository;
        this.dataProvider = new OffersDataProvider(offerService, menuService);
        this.menuService = menuService;
        this.menuRepository = menuRepository;
        this.deleteButton = new Button();
        this.generatePDF = new Button("Generate PDF"); // Initialisiere den Button
        generatePDF.setVisible(true); // Standardmäßig unsichtbar
        generatePDF.addClickListener(event ->
        {
            createMenuPdf();

        }); // Button-Click-Listener hinzufügen

        setClassName("content");
        VerticalLayout pageVerticalLayout = new VerticalLayout();
        pageVerticalLayout.setSizeFull();

        this.filterDate = new DatePicker("Datum:");
        this.filterDate.setWeekNumbersVisible(true);
        this.filterDate.setI18n(new DatePickerLocaleGerman());

        // Neue ComboBox für Field hinzufügen
        fieldComboBox = new ComboBox<>("Field auswählen:");
        fieldComboBox.setItemLabelGenerator(Field::getName); // Anzeige in der ComboBox
        fieldComboBox.setItems(fieldService.findAll());
        fieldComboBox.setValue(fieldService.findAll().get(0)); // Setze Standardwert

        fieldComboBox.addValueChangeListener(event ->
        {
            Field selectedField = event.getValue();
            if(selectedField != null)
            {
                //String fieldName = selectedField.getName().toLowerCase();
                //boolean shouldShowButton = fieldName.matches("^(kit|kind|gri|gru).*");
                boolean shouldShowButton = List.of(DefaultField.GRUNDSCHULE, DefaultField.KINDERTAGESSTÄTTE, DefaultField.KINDERGARTEN)
                                .stream().anyMatch(defaultField -> defaultField.equalsDefault(selectedField));

                generatePDF.setVisible(shouldShowButton);
                // Setze die Field-ID und aktualisiere die Daten
                dataProvider.setFieldId(selectedField.getId());
                // Benachrichtige das Grid, dass es sich aktualisieren soll
                dataProvider.refreshAll();
            }else
            {
                generatePDF.setVisible(false); // Verstecke den Button, wenn nichts ausgewählt ist
            }
        });


        ComboBox<WeekSelector> weekCombobox = getWeekSelectorComboBox();

        this.filterDate.addValueChangeListener(event ->
        {
            this.vonDate = event.getValue().minusDays(event.getValue().getDayOfWeek().getValue() - 1);
            var value = weekCombobox.getValue();
            this.bisDate = this.vonDate.plus(value.amount(), value.unit());
            this.dataProvider.setDateRange(this.vonDate, this.bisDate);
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        // Grid-Setup
//        Grid<Week> grid = new Grid<>(Week.class, false);
//        grid.addColumn(Week::getKw).setHeader("KW");
//        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item.getDayFor(DayOfWeek.MONDAY).getDate(), formatter))).setHeader("Montag");
//        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item.getDayFor(DayOfWeek.TUESDAY).getDate(), formatter))).setHeader("Dienstag");
//        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item.getDayFor(DayOfWeek.WEDNESDAY).getDate(), formatter))).setHeader("Mittwoch");
//        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item.getDayFor(DayOfWeek.THURSDAY).getDate(), formatter))).setHeader("Donnerstag");
//        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item.getDayFor(DayOfWeek.FRIDAY).getDate(), formatter))).setHeader("Freitag");
//        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item.getDayFor(DayOfWeek.SATURDAY).getDate(), formatter))).setHeader("Samstag");
//        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(item.getDayFor(DayOfWeek.SUNDAY).getDate(), formatter))).setHeader("Sonntag");

        Grid<LocalDate> grid = new Grid<>(LocalDate.class, false);
        grid.addColumn(localDate -> Week.getKw(localDate)).setHeader("KW");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(Week.getDate(DayOfWeek.MONDAY, item), formatter))).setHeader("Montag");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(Week.getDate(DayOfWeek.TUESDAY, item), formatter))).setHeader("Dienstag");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(Week.getDate(DayOfWeek.WEDNESDAY, item), formatter))).setHeader("Mittwoch");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(Week.getDate(DayOfWeek.THURSDAY, item), formatter))).setHeader("Donnerstag");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(Week.getDate(DayOfWeek.FRIDAY, item), formatter))).setHeader("Freitag");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(Week.getDate(DayOfWeek.SATURDAY, item), formatter))).setHeader("Samstag");
        grid.addColumn(new ComponentRenderer<>(item -> createDayCell(Week.getDate(DayOfWeek.SUNDAY, item), formatter))).setHeader("Sonntag");

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

        System.out.println(new StringBuilder().repeat("-", 500).append("Oben: ").append(field.getName()));

        for(LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1))
        {
            if(date.getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY))
            {
                continue;
            }

            Optional<Offer> offer = offerService.findByLocalDateAndFieldId(date, field.getId());
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
        CreateMenuPdf.generatePdf(menuList, field.getName());
    }


    private ComboBox<WeekSelector> getWeekSelectorComboBox()
    {

        ComboBox<WeekSelector> weekCombobox = getSelectorComboBox();
        weekCombobox.addValueChangeListener(event ->
        {
            if(!DayOfWeek.MONDAY.equals(this.filterDate.getValue().getDayOfWeek()))
            {
                this.vonDate = this.filterDate.getValue().minusDays(this.filterDate.getValue().getDayOfWeek().getValue() - 1);
            }
            this.vonDate = this.filterDate.getValue();
            WeekSelector value = event.getValue();
            this.bisDate = this.vonDate.plus(value.amount(), value.unit());
            this.dataProvider.setDateRange(this.vonDate, this.bisDate);
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
            Offer newOffer = Offer.builder()
                    .localDate(date)
                    .field(fieldComboBox.getValue())
                    .menus(new HashSet<>()) // Leere Menge für Menüs
                    .build();
            return offerService.update(newOffer); // Speichern und zurückgeben
        });

        this.deleteButton.setEnabled(offer.getLocalDate().isAfter(LocalDate.now()));

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

        LocalDate localDate = LocalDate.parse(dropZone.getElement().getAttribute("date"));
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
                            Offer newOffer = Offer.builder()
                                    .localDate(dropDate)
                                    .field(selectedField)
                                    .menus(new HashSet<>()) // Neues Offer hat noch keine Menüs
                                    .build();
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

        this.deleteButton = new Button(VaadinIcon.TRASH.create());

        this.deleteButton.setEnabled(offer.getLocalDate().isAfter(LocalDate.now()));

        this.deleteButton.addClickListener(event ->
        {
            Div parent = (Div) wrapper.getParent().orElse(null);
            if(parent != null)
            {
                parent.remove(wrapper);
                updateDropTargets(parent);

                // Menü aus Offer entfernen
                offerService.removeMenuFromOffer(offer.getId(), item.getItem().getId());
                System.out.println(offer.getId() + " : " + item.getItem().getId());
            }
        });

        HorizontalLayout layout = new HorizontalLayout(item, deleteButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        layout.setWidthFull();

        wrapper.add(layout);
        return wrapper;
    }


    private static class MenuDiv extends Div
    {
        private final Menu item;

        public MenuDiv(Menu item)
        {
            this.item = item;
            Span name = new Span(item.getName());
            //name.getElement().setAttribute("title", item.getDescription()); // Tooltip mit Beschreibung
            this.add(name);
        }

        public Menu getItem()
        {
            return this.item;
        }

    }

    private record WeekSelector(int amount, ChronoUnit unit, String name)
    {

    }

}

