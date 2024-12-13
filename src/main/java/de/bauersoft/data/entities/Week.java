package de.bauersoft.data.entities;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private LocalDate mon;
    @Transient
    private LocalDate tue;
    @Transient
    private LocalDate wed;
    @Transient
    private LocalDate thu;
    @Transient
    private LocalDate fri;
    @Transient
    private LocalDate sat;
    @Transient
    private LocalDate sun;

    @Transient
    private Map<DayOfWeek, Day> dayOfWeekMap;

    public int getKw() {
        return kw;
    }

    public void setKw(int kw) {
        this.kw = kw;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
        refreshDayOfWeekMap(); // Map aktualisieren
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public LocalDate getMon() {
        return mon;
    }

    public void setMon(LocalDate mon) {
        this.mon = mon;
    }

    public LocalDate getTue() {
        return tue;
    }

    public void setTue(LocalDate tue) {
        this.tue = tue;
    }

    public LocalDate getWed() {
        return wed;
    }

    public void setWed(LocalDate wed) {
        this.wed = wed;
    }

    public LocalDate getThu() {
        return thu;
    }

    public void setThu(LocalDate thu) {
        this.thu = thu;
    }

    public LocalDate getFri() {
        return fri;
    }

    public void setFri(LocalDate fri) {
        this.fri = fri;
    }

    public LocalDate getSat() {
        return sat;
    }

    public void setSat(LocalDate sat) {
        this.sat = sat;
    }

    public LocalDate getSun() {
        return sun;
    }

    public void setSun(LocalDate sun) {
        this.sun = sun;
    }

    private void refreshDayOfWeekMap() {
        dayOfWeekMap = days.stream()
                .collect(Collectors.toMap(day -> day.getDate().getDayOfWeek(), day -> day));
    }

    public Day getDayFor(DayOfWeek dayOfWeek) {
        if (dayOfWeekMap == null) {
            refreshDayOfWeekMap();
        }
        return dayOfWeekMap.get(dayOfWeek);
    }

    public void addDay(Day day) {
        days.add(day);
        if (dayOfWeekMap == null) {
            refreshDayOfWeekMap();
        }
        dayOfWeekMap.put(day.getDate().getDayOfWeek(), day);
    }

    public void removeDay(Day day) {
        days.remove(day);
        if (dayOfWeekMap != null) {
            dayOfWeekMap.remove(day.getDate().getDayOfWeek());
        }
    }
}
