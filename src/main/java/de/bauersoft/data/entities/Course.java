package de.bauersoft.data.entities;

import java.util.Objects;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "course")
public class Course extends AbstractEntity
{

	private String name;

	@OneToMany(mappedBy = "course", fetch = FetchType.EAGER)
	private Set<Component> component;
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Set<Component> getComponent()
	{
		return component;
	}

	public Course setComponent(Set<Component> component)
	{
		this.component = component;
		return this;
	}

	@Override
	public boolean equals(Object o)
	{
		if(o == null || getClass() != o.getClass())
		{
			return false;
		}

		if(!super.equals(o))
		{
			return false;
		}

		Course course = (Course) o;
		return Objects.equals(name, course.name);
	}

	@Override
	public String toString()
	{
		return "Course{" +
				"name='" + name + '\'' +
				'}';
	}
}
