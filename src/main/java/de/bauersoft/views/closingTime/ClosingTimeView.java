package de.bauersoft.views.closingTime;

import com.vaadin.componentfactory.EnhancedDateRangePicker;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.data.entities.institutionClosingTime.InstitutionClosingTime;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.providers.InstitutionClosingTimeDataProvider;
import de.bauersoft.security.AuthenticatedUser;
import de.bauersoft.services.InstitutionClosingTimeService;
import de.bauersoft.services.InstitutionService;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

@PageTitle("Schließtage")
@Route(value = "closingtime", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "INSTITUTION"})
public class ClosingTimeView extends Div
{
    public static final EnhancedDateRangePicker.DatePickerI18n i18n;
    private static final DateTimeFormatter formatter;

    static
    {
        i18n = new EnhancedDateRangePicker.DatePickerI18n();
        i18n.setWeekdays(Arrays.asList("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"))
                .setWeekdaysShort(Arrays.asList("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"))
                .setMonthNames(Arrays.asList("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"))
                .setToday("Heute")
                .setCancel("Abbrechen")
                .setFirstDayOfWeek(DayOfWeek.MONDAY.getValue());

        formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY);
    }

    private final AuthenticatedUser authenticatedUser;

    private final InstitutionClosingTimeService closingTimeService;
    private final InstitutionService institutionService;

    private final InstitutionClosingTimeDataProvider closingTimeDataProvider;

    private User user;

    private HorizontalLayout buttonLayout;
    private Button addButton;
    private AutofilterGrid<InstitutionClosingTime, Long> grid;

    private ClosingTimeManager closingTimeManager;

    public ClosingTimeView(AuthenticatedUser authenticatedUser, InstitutionClosingTimeService closingTimeService, InstitutionService institutionService, InstitutionClosingTimeDataProvider closingTimeDataProvider)
    {
        this.institutionService = institutionService;
        this.closingTimeDataProvider = closingTimeDataProvider;
        setClassName("content");

        this.authenticatedUser = authenticatedUser;
        this.closingTimeService = closingTimeService;

        if(authenticatedUser.get().isEmpty()) return;
        this.user = authenticatedUser.get().get();

        closingTimeManager = new ClosingTimeManager(this, authenticatedUser, user, closingTimeService, institutionService, closingTimeDataProvider);

        this.add(closingTimeManager);

//        if(authenticatedUser.get().isEmpty()) return;
//        this.user = authenticatedUser.get().get();
//
//
//
//        buttonLayout = new HorizontalLayout();
//        buttonLayout.getStyle()
//                .setJustifyContent(Style.JustifyContent.CENTER);
//
//        addButton = new Button("Schließzeitraum hinzufügen");
//        addButton.getStyle()
//                .setFontSize("var(--lumo-font-size-xl)")
//                .setBorder("1px solid grey");
//
//        buttonLayout.add(addButton);
//
//        grid = new AutofilterGrid<>(closingTimeService.getRepository());
//        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
//        grid.setSizeFull();
//
//        grid.addComponentColumn("Löschen", "4em", institutionClosingTime ->
//        {
//            Button button = new Button(LineAwesomeIcon.TRASH_SOLID.create());
//            button.setWidth("4em");
//
//
//
//            return button;
//        });
//
//        grid.addColumn("header", "Beschreibung", InstitutionClosingTime::getHeader);
//        grid.addColumn("startDate", "Startdatum", institutionClosingTime ->
//        {
//            return institutionClosingTime.getStartDate().format(formatter).toString();
//        }, (institutionClosingTimeRoot, path, criteriaQuery, criteriaBuilder) ->
//        {
//            return criteriaBuilder.function("DATE_FORMAT", String.class, path, criteriaBuilder.literal("%d.%m.%Y"));
//        });
//
//        grid.addColumn("endDate", "Enddatum", institutionClosingTime ->
//        {
//            return institutionClosingTime.getEndDate().format(formatter).toString();
//        }, (institutionClosingTimeRoot, path, criteriaQuery, criteriaBuilder) ->
//        {
//            return criteriaBuilder.function("DATE_FORMAT", String.class, path, criteriaBuilder.literal("%d.%m.%Y"));
//        });
//
//        grid.addGridContextMenu("Neuer Schließzeitraum", event ->
//        {
//            new ClosingTimeDialog(this, new InstitutionClosingTime(), DialogState.NEW, closingTimeService);
//
//        }, "Löschen", event ->
//        {
//
//        });
//
//        GridMenuItem<InstitutionClosingTime> openItem = grid.getGridContextMenu().addItem("Öffnen", event ->
//        {
//            Optional<InstitutionClosingTime> item = event.getItem();
//
//            if(item.isEmpty()) return;
//            new ClosingTimeDialog(this, item.get(), DialogState.EDIT, closingTimeService);
//        });
//
//        grid.getGridContextMenu().addGridContextMenuOpenedListener(event ->
//        {
//            openItem.setVisible(event.getItem().isPresent());
//        });
//
//        grid.addItemDoubleClickListener(event ->
//        {
//            new ClosingTimeDialog(this, event.getItem(), DialogState.EDIT, closingTimeService);
//        });
//
//        this.add(buttonLayout, grid);
    }

    public AutofilterGrid<InstitutionClosingTime, Long> getGrid()
    {
        return grid;
    }
}
