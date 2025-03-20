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
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

@PageTitle("Schließtage")
@Route(value = "closingtime", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "INSTITUTION"})
@Getter
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

    private User user;

    private ClosingTimeManager closingTimeManager;

    public ClosingTimeView(AuthenticatedUser authenticatedUser, InstitutionClosingTimeService closingTimeService, InstitutionService institutionService)
    {
        this.institutionService = institutionService;
        setClassName("content");

        this.authenticatedUser = authenticatedUser;
        this.closingTimeService = closingTimeService;

        if(authenticatedUser.get().isEmpty()) return;
        this.user = authenticatedUser.get().get();

        closingTimeManager = new ClosingTimeManager(this, authenticatedUser, user, closingTimeService, institutionService);

        this.add(closingTimeManager);
    }
}
