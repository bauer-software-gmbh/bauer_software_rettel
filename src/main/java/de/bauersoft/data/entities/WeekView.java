package de.bauersoft.data.entities;

import java.time.LocalDate;

public class WeekView {
    private int kw;          // Kalenderwoche
    private int year;        // Jahr
    private LocalDate mon;
    private LocalDate tue;
    private LocalDate wed;
    private LocalDate thu;
    private LocalDate fri;
    private LocalDate sat;
    private LocalDate sun;

    public WeekView(int kw, int year, LocalDate mon, LocalDate tue, LocalDate wed, LocalDate thu, LocalDate fri, LocalDate sat, LocalDate sun) {
        this.kw = kw;
        this.year = year;
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.thu = thu;
        this.fri = fri;
        this.sat = sat;
        this.sun = sun;
    }

    // Getter und Setter
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
}
