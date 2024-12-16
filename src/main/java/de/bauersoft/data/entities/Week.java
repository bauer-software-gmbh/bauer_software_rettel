package de.bauersoft.data.entities;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "week")
public class Week extends AbstractEntity {

    @Column(nullable = false)
    private int kw;

    @Column(nullable = false)
    private int year; // Jahr

    @OneToMany(mappedBy = "week", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Day> days = new ArrayList<>();

    @Transient
    private Map<DayOfWeek, Day> dayOfWeekMap = new HashMap<>();

    @Transient
    private LocalDate startDate;

    public Week(LocalDate date) {
        startDate = date;
        dayOfWeekMap.put(DayOfWeek.MONDAY, new Day(startDate));
        dayOfWeekMap.put(DayOfWeek.TUESDAY, new Day(startDate.plusDays(1)));
        dayOfWeekMap.put(DayOfWeek.WEDNESDAY, new Day(startDate.plusDays(2)));
        dayOfWeekMap.put(DayOfWeek.THURSDAY, new Day(startDate.plusDays(3)));
        dayOfWeekMap.put(DayOfWeek.FRIDAY, new Day(startDate.plusDays(4)));
        dayOfWeekMap.put(DayOfWeek.SATURDAY, new Day(startDate.plusDays(5)));
        dayOfWeekMap.put(DayOfWeek.SUNDAY, new Day(startDate.plusDays(6)));

    }

    public Week() {
        this(LocalDate.now());
    }

    public int getKw() {
        return kw;
    }

    public void setKw(int kw) {
        this.kw = kw;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Day getDayFor(DayOfWeek dayOfWeek) {
        return dayOfWeekMap.get(dayOfWeek);
    }

    public void addDay(Day day) {
        days.add(day);
        dayOfWeekMap.put(day.getDate().getDayOfWeek(), day);
    }

    public void clearDays() {
        dayOfWeekMap.put(DayOfWeek.MONDAY, new Day(startDate));
        dayOfWeekMap.put(DayOfWeek.TUESDAY, new Day(startDate.plusDays(1)));
        dayOfWeekMap.put(DayOfWeek.WEDNESDAY, new Day(startDate.plusDays(2)));
        dayOfWeekMap.put(DayOfWeek.THURSDAY, new Day(startDate.plusDays(3)));
        dayOfWeekMap.put(DayOfWeek.FRIDAY, new Day(startDate.plusDays(4)));
        dayOfWeekMap.put(DayOfWeek.SATURDAY, new Day(startDate.plusDays(5)));
        dayOfWeekMap.put(DayOfWeek.SUNDAY, new Day(startDate.plusDays(6)));
    }

    @Override
    public String toString() {
        return "Week{id=" + getId() + ", kw='" + getKw() + "'}"; // Nur Felder, die sicher geladen sind
    }
}
