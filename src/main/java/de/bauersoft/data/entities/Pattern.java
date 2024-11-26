package de.bauersoft.data.entities;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "pattern")
public class Pattern extends AbstractEntity {
	
	String name;
	String description ="";
	Character religious ='N';
	
	@ManyToMany(mappedBy = "patterns",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Set<Recipe> recipes;
	
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
	public Character getReligious() {
		return religious;
	}
	public void setReligious(Character religious) {
		this.religious = religious;
	}
	public Set<Recipe> getRecipes() {
		return recipes;
	}
	public void setRecipes(Set<Recipe> recipes) {
		this.recipes = recipes;
	}
	
	
}
