package de.bauersoft.views.closingTime.institutionLayer.dialogLayer;

import com.vaadin.componentfactory.DateRange;
import com.vaadin.componentfactory.EnhancedDateRangePicker;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.dom.Style;
import de.bauersoft.data.entities.institutionClosingTime.InstitutionClosingTime;
import de.bauersoft.services.InstitutionClosingTimeService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.closingTime.ClosingTimeManager;
import de.bauersoft.views.closingTime.institutionLayer.InstitutionTab;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.DayOfWeek;
import java.time.LocalDate;
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

    private final ClosingTimeManager manager;
    private final InstitutionTab institutionTab;
    private final InstitutionClosingTime item;
    private DialogState dialogState;

    private final InstitutionClosingTimeService closingTimeService;

    private final Binder<InstitutionClosingTime> binder;

    private final TextField descriptionField;

    private final EnhancedDateRangePicker dateRangePicker;
    private final Div infoIcon;
    private final HorizontalLayout dateRangePickerLayout;

    public ClosingTimeDialog(ClosingTimeManager manager, InstitutionTab institutionTab, InstitutionClosingTime item, DialogState dialogState)
    {
        this.manager = manager;
        this.institutionTab = institutionTab;
        this.item = item;
        this.dialogState = dialogState;

        closingTimeService = manager.getClosingTimeService();

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

        infoIcon = new Div(VaadinIcon.INFO_CIRCLE.create());
        infoIcon.getElement().addEventListener("mouseover", event ->
        {
            infoIcon.getElement().setProperty("title", """
                    Um einen Schließzeitraum von einem Tag zu erstellen, genügt es, 
                    in der Kalenderübersicht den gewünschten Tag zu doppelklicken.
                    """);
        });

        infoIcon.getElement().addEventListener("mouseout", event ->
        {
            infoIcon.getElement().removeProperty("title");
        });

        dateRangePickerLayout = new HorizontalLayout(dateRangePicker, infoIcon);
        dateRangePickerLayout.setWidthFull();
        dateRangePickerLayout.getStyle()
                        .setAlignItems(Style.AlignItems.CENTER);

        binder.forField(descriptionField)
                .asRequired("Beschreibung darf nicht leer sein")
                .bind(InstitutionClosingTime::getHeader, InstitutionClosingTime::setHeader);

        binder.forField(dateRangePicker)
                .withValidator((value, context) ->
        {
            LocalDate startDate = value.getStartDate();
            LocalDate endDate = value.getEndDate();

            if(startDate == null)
                return ValidationResult.error("Startdatum darf nicht leer sein");

            if(endDate != null && startDate.isAfter(endDate))
                return ValidationResult.error("Startdatum muss vor dem Enddatum liegen");

            return ValidationResult.ok();

        }).bind(institutionClosingTime ->
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

                item.setInstitution(institutionTab.getInstitution());

                closingTimeService.update(item);
                institutionTab.getGrid().refreshAll();

                Notification.show("Daten wurden aktualisiert");
                this.close();

            }catch(DataIntegrityViolationException error)
            {
                Notification.show("Doppelter Eintrag", 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);

            }catch(ValidationException ex)
            {

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

        this.add(descriptionField, dateRangePickerLayout);

        this.getFooter().add(saveButton, cancelButton);
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);
        this.setModal(true);
        this.open();
    }
}
