package de.bauersoft.data.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu")
public class Menu extends AbstractEntity{

	@Column(nullable = false)
	private String name; // Name des Menüs

	@Column(nullable = false, columnDefinition = "TEXT")
	private String description = ""; // Beschreibung des Menüs

	@ManyToMany(mappedBy = "menus")
	private List<Day> days = new ArrayList<>(); // Tage, an denen dieses Menü verfügbar ist


	// Set<Offer> offers;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public List<Day> getDays() {
		return days;
	}

	public void setDays(List<Day> days) {
		this.days = days;
	}
}
