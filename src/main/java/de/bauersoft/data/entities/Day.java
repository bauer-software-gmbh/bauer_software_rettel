package de.bauersoft.data.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "day")
public class Day extends AbstractEntity implements Serializable {

    private String name; // Name des Tages, z. B. "Montag"
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "week_id", nullable = false)
    private Week week; // Zugehörige Woche

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Week getWeek() {
        return week;
    }

    public void setWeek(Week week) {
        this.week = week;
    }
}
