package de.bauersoft.data.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "day")
public class Day extends AbstractEntity {

    @Column(nullable = false)
    private String name; // Name des Tages (z.B. Montag)

    @Column(nullable = false)
    private LocalDate date; // Datum des Tages

    @ManyToOne
    @JoinColumn(name = "week_id", nullable = false)
    private Week week; // Zugehörige Woche

    @ManyToMany
    @JoinTable(
            name = "day_menu",
            joinColumns = @JoinColumn(name = "day_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_id")
    )
    private List<Menu> menus = new ArrayList<>(); // Menüs, die an diesem Tag verfügbar sind

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

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }
}
