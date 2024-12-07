package de.bauersoft.data.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.Set;

@Entity
@Table(name = "menu")
public class Menu extends AbstractEntity
{
	private String name;
	private String description;

	@Transient
	private Set<Course> courses;


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

	public Set<Course> getCourses()
	{
		return courses;
	}

	public void setCourses(Set<Course> courses)
	{
		this.courses = courses;
	}
}
