package de;

import com.sun.jna.platform.win32.OaIdl;
import com.vaadin.flow.component.datepicker.DatePicker;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class DateTimeUtils
{
    public static final DateTimeFormatter DATE_FORMATTER;
    public static final DateTimeFormatter TIME_FORMATTER;
    public static final DateTimeFormatter DATE_TIME_FORMATTER;

    public static final DatePicker.DatePickerI18n DATE_PICKER_I18N;

    static
    {
        DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
        DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        DATE_PICKER_I18N = new DatePicker.DatePickerI18n()
                .setDateFormat("dd.MM.yyyy")
                .setToday("Heute")
                .setCancel("Abbruch")
                .setFirstDayOfWeek(1)
                .setMonthNames(Arrays.asList("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"))
                .setWeekdays(Arrays.asList("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"))
                .setWeekdaysShort(Arrays.asList("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"));
    }
}
