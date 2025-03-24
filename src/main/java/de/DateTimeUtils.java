package de;

import com.vaadin.flow.component.datepicker.DatePicker;

import java.util.Arrays;

public class DateTimeUtils
{
    public static final DatePicker.DatePickerI18n datePickerI18n;

    static
    {
        datePickerI18n = new DatePicker.DatePickerI18n()
                .setDateFormat("dd.MM.yyyy")
                .setToday("Heute")
                .setCancel("Abbruch")
                .setFirstDayOfWeek(1)
                .setMonthNames(Arrays.asList("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"))
                .setWeekdays(Arrays.asList("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"))
                .setWeekdaysShort(Arrays.asList("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"));
    }
}
