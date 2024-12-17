package de.bauersoft.data.entities.menu;

import de.bauersoft.data.entities.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "menu")
public class Menu extends AbstractEntity
{
	private String name;
	private String description;

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
		Menu menu = (Menu) o;
		return Objects.equals(name, menu.name) && Objects.equals(description, menu.description);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), name, description);
	}

	@Override
	public String toString()
	{
		return "Menu{" +
				"name='" + name + '\'' +
				", description='" + description + '\'' +
				'}';
	}
}
