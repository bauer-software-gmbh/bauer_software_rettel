package de.bauersoft.data.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "menu")
public class Menu extends AbstractEntity{
	
	String name;
	String description ="";
	
	
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
}
