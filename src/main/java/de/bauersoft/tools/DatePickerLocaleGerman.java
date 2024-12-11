package de.bauersoft.tools;

import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;

public class DatePickerLocaleGerman  extends DatePickerI18n{

    private static final long serialVersionUID = -6001243972599564535L;
    @Override
    public List<String> getWeekdaysShort() {
        String[] weekdaysShort = {"So","Mo","Di","Mi","Do","Fr","Sa"};
        return Arrays.asList(weekdaysShort);
    }
    @Override
    public int getFirstDayOfWeek() {
        return 1 ;
    }
    @Override
    public String getCancel() {
        return "Abbruch";
    }
    @Override
    public String getToday() {
        return "Heute";
    }
    @Override
    public List<String> getWeekdays() {
        String[] weekdays = {"Sonntag","Montag","Dienstag","Mittwoch","Donnerstag","Freitag","Samstag"};
        return Arrays.asList(weekdays);
    }
    @Override
    public List<String> getMonthNames() {
        String[] monthnames = {"Januar","Februar","März","April","Mai","Juni","Juli","August", "September", "Oktober", "November", "Dezember"};
        return Arrays.asList(monthnames);
    }
}
