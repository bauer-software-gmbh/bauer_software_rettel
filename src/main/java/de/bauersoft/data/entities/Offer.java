package de.bauersoft.data.entities;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "offer")
public class Offer extends AbstractEntity {

	LocalDate date;
	
	
	// Set<Menu> menus;
}
