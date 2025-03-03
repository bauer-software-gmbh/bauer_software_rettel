package de.bauersoft.views.closingTime;

import com.vaadin.componentfactory.EnhancedDateRangePicker;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.institutionClosingTime.InstitutionClosingTime;
import de.bauersoft.services.InstitutionClosingTimeService;
import de.bauersoft.views.MainLayout;
import de.bauersoft.views.institution.institutionFields.components.closingTime.ClosingTimesComponent;
import de.bauersoft.views.institution.institutionFields.components.closingTime.ClosingTimesContainer;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@PageTitle("Schließtage")
@Route(value = "closingtime", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "INSTITUTION"})
public class ClosingTimeView extends Div
{
    public static final EnhancedDateRangePicker.DatePickerI18n i18n;

    static
    {
        i18n = new EnhancedDateRangePicker.DatePickerI18n();
        i18n.setWeekdays(Arrays.asList("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"))
                .setWeekdaysShort(Arrays.asList("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"))
                .setMonthNames(Arrays.asList("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"))
                .setToday("Heute")
                .setCancel("Abbrechen")
                .setFirstDayOfWeek(DayOfWeek.MONDAY.getValue());
    }

    private final InstitutionClosingTimeService institutionClosingTimeService;

    private final List<ClosingTimesComponent.ClosingTimeLine> closingTimeLines;
    private final Button addButton;

    public ClosingTimeView(InstitutionClosingTimeService institutionClosingTimeService)
    {
        this.institutionClosingTimeService = institutionClosingTimeService;

        setClassName("content");

        closingTimeLines = new ArrayList<>();

        addButton = new Button("Schließzeitraum hinzufügen", LineAwesomeIcon.PLUS_SOLID.create());
        this.add(addButton);
    }
}
