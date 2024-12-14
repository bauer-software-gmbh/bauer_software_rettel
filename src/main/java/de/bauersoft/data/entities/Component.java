package de.bauersoft.data.entities;

import java.util.List;
import java.util.Set;

import de.bauersoft.data.entities.menu.MenuPatternComponents;
import jakarta.persistence.*;

@Entity
@Table(name="component")
public class Component extends AbstractEntity {
	
	private String name;
	private String description ="";

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinTable(
			name = "component_recipes",
			joinColumns = @JoinColumn(name = "component_id"),
			inverseJoinColumns = @JoinColumn(name = "recipe_id")
	)
	private Set<Recipe> recipes;

	@ManyToOne(optional = true, targetEntity = Course.class, fetch = FetchType.EAGER)
	private Course course;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
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

	public Set<Recipe> getRecipes()
	{
		return recipes;
	}

	public void setRecipes(Set<Recipe> recipes)
	{
		this.recipes = recipes;
	}

	public Course getCourse()
	{
		return course;
	}

	public void setCourse(Course course)
	{
		this.course = course;
	}


	@Override
	public String toString()
	{
		return "Component{" +
				"name='" + name + '\'' +
				'}';
	}
}
