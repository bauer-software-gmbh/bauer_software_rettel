package de.bauersoft.data.entities.pattern;

import java.util.Objects;
import java.util.Set;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.Recipe;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "pattern")
public class Pattern extends AbstractEntity
{
	private String name;
	private String description ="";
	private Character religious ='N';
	
	@ManyToMany(mappedBy = "patterns",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Set<Recipe> recipes;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		Objects.requireNonNull(name, "name cannot be null.");
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Character getReligious()
	{
		return religious;
	}

	public void setReligious(Character religious)
	{
		Objects.requireNonNull(religious, "religious cannot be null.");
		this.religious = religious;
	}

	public Set<Recipe> getRecipes()
	{
		return recipes;
	}

	public void setRecipes(Set<Recipe> recipes)
	{
		this.recipes = recipes;
	}

	public boolean equalsDefault(DefaultPattern defaultPattern)
	{
		return name.equals(defaultPattern.getName());
	}
}
