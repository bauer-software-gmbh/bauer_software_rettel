package de.bauersoft.services.offer;

import de.bauersoft.data.entities.offer.Offer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Week
{
    private LocalDate dateOfWeek;

    private int kw;
    private Map<DayOfWeek, LocalDate> days;

    public Week(LocalDate dateOfWeek)
    {
        this.dateOfWeek = dateOfWeek;

        kw = -1;
        days = new HashMap<>();

        kw = getKw(dateOfWeek);
        for(DayOfWeek dayOfWeek : DayOfWeek.values())
            days.put(dayOfWeek, getDate(dayOfWeek, dateOfWeek));
    }

    public LocalDate getDateOfWeek()
    {
        return dateOfWeek;
    }

    public int getKw()
    {
        return kw;
    }

    public void setKw(int kw)
    {
        this.kw = kw;
    }

    public LocalDate getDate(DayOfWeek wanted)
    {
        return days.get(wanted);
    }

    public Map<DayOfWeek, LocalDate> getDays()
    {
        return days;
    }

    public static LocalDate getDate(DayOfWeek wanted, LocalDate given)
    {
        Objects.requireNonNull(wanted, "wanted must not be null");
        Objects.requireNonNull(given, "given must not be null");

        DayOfWeek givenDay = given.getDayOfWeek();

        int daysDifference = wanted.getValue() - givenDay.getValue();

        return given.plusDays(daysDifference);
    }

    public static int getKw(LocalDate given)
    {
        return given.get(WeekFields.of(Locale.GERMANY).weekOfWeekBasedYear());
    }

    public void clearDays() {
        days.clear();
    }

    public void addDay(Offer offer)
    {
        days.put(offer.getLocalDate().getDayOfWeek(), offer.getLocalDate());
    }
}
