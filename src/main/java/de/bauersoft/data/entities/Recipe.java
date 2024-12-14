package de.bauersoft.data.entities;

import java.util.Set;

import de.bauersoft.data.entities.pattern.Pattern;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "recipe")
public class Recipe extends AbstractEntity{
	
	private String name;
	private String description ="";
	
	@Transient
	private Set<Formulation> formulation;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinTable(
			name = "recipe_patterns",
			joinColumns = @JoinColumn(name = "recipe_id"),
			inverseJoinColumns = @JoinColumn(name = "pattern_id")
	)
	private Set<Pattern> patterns;

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



	public Set<Formulation> getFormulation() {
		return formulation;
	}

	public void setFormulations(Set<Formulation> formulation) {
		this.formulation = formulation;
	}

	public Set<Pattern> getPatterns() {
		return patterns;
	}

	public void setPatterns(Set<Pattern> patterns) {
		this.patterns = patterns;
	}

}
