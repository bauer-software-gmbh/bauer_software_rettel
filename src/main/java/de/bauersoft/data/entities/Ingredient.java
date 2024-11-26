package de.bauersoft.data.entities;

import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ingredient")
public class Ingredient extends AbstractGroupByEntity<Ingredient>{

	private String name;
	private String description ="";
	
	@OneToOne(cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_id", referencedColumnName = "id")
	private Unit unit;
	
	@ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	@JoinTable(name = "allergenic", 
	joinColumns = @JoinColumn(name = "ingredient_id"), 
	inverseJoinColumns = @JoinColumn(name = "allergen_id"))
	private Set<Allergen> allergens;
	
	@ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	@JoinTable(name = "additives", 
	joinColumns = @JoinColumn(name = "ingredient_id"), 
	inverseJoinColumns = @JoinColumn(name = "additive_id"))
	private Set<Additive> additives;
	
	@OneToMany(mappedBy="ingredient",cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
	private Set<Formulation> formulations;
	
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

	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Set<Allergen> getAllergens() {
		return allergens;
	}
	public void setAllergens(Set<Allergen> allergens) {
		this.allergens = allergens;
	}
	public Set<Additive> getAdditives() {
		return additives;
	}
	public void setAdditives(Set<Additive> additives) {
		this.additives = additives;
	}
	
	public Set<Formulation> getFormulations() {
		return formulations;
	}
	public void setFormulations(Set<Formulation> formulations) {
		this.formulations = formulations;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if(obj instanceof Ingredient other) {
			return Objects.equals(super.getId(),other.getId());
		}
		return false;
	}
}
