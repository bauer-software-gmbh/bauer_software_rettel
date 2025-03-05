package de.bauersoft.views.closingTime.institutionLayer.dialogLayer;

import com.vaadin.componentfactory.DateRange;
import com.vaadin.componentfactory.EnhancedDateRangePicker;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import de.bauersoft.data.entities.institutionClosingTime.InstitutionClosingTime;
import de.bauersoft.services.InstitutionClosingTimeService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.closingTime.ClosingTimeManager;
import de.bauersoft.views.closingTime.ClosingTimeView;
import de.bauersoft.views.closingTime.institutionLayer.InstitutionTab;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Locale;

public class ClosingTimeDialog extends Dialog
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

    private final ClosingTimeManager closingTimeManager;
    private final InstitutionTab institutionTab;
    private final InstitutionClosingTime item;
    private DialogState dialogState;

    private final InstitutionClosingTimeService closingTimeService;

    private final Binder<InstitutionClosingTime> binder;

    private final TextField descriptionField;
    private final EnhancedDateRangePicker dateRangePicker;


    public ClosingTimeDialog(ClosingTimeManager closingTimeManager, InstitutionTab institutionTab, InstitutionClosingTime item, DialogState dialogState)
    {
        this.closingTimeManager = closingTimeManager;
        this.institutionTab = institutionTab;
        this.item = item;
        this.dialogState = dialogState;

        closingTimeService = closingTimeManager.getClosingTimeService();

        this.setHeaderTitle(dialogState.toString());

        binder = new Binder<>();

        descriptionField = new TextField();
        descriptionField.setWidthFull();
        descriptionField.setMaxLength(64);
        descriptionField.setPlaceholder("Sommerferien, Winterferien, o. s. ä.");

        dateRangePicker = new EnhancedDateRangePicker();
        dateRangePicker.setLocale(Locale.GERMAN);
        dateRangePicker.setPattern("dd.MM.yyyy");
        dateRangePicker.setI18n(i18n);
        dateRangePicker.setWidthFull();

        binder.bind(descriptionField, InstitutionClosingTime::getHeader, InstitutionClosingTime::setHeader);
        binder.forField(dateRangePicker).bind(institutionClosingTime ->
        {
            return new DateRange(institutionClosingTime.getStartDate(), institutionClosingTime.getEndDate());

        }, (institutionClosingTime, dateRange) ->
        {
            institutionClosingTime.setStartDate(dateRange.getStartDate());
            institutionClosingTime.setEndDate(dateRange.getEndDate());
        });

        binder.readBean(item);

        Button saveButton = new Button("Speichern");
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.setMinWidth("150px");
        saveButton.setMaxWidth("180px");
        saveButton.addClickListener(e ->
        {
            binder.validate();
            if(!binder.isValid()) return;

            try
            {
                binder.writeBean(item);

                closingTimeService.update(binder.getBean());
                institutionTab.getGrid().refreshAll();

                Notification.show("Daten wurden aktualisiert");
                this.close();

            }catch(DataIntegrityViolationException error)
            {
                Notification.show("Doppelter Eintrag", 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);

            }catch(ValidationException ex)
            {
                ex.printStackTrace();
            }
        });

        Button cancelButton = new Button("Abbrechen");
        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.setMinWidth("150px");
        cancelButton.setMaxWidth("180px");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(e ->
        {
            binder.removeBean();
            institutionTab.getGrid().refreshAll();
            this.close();
        });

        this.add(descriptionField, dateRangePicker);

        this.getFooter().add(saveButton, cancelButton);
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);
        this.setModal(true);
        this.open();
    }
}
