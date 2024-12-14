package de.bauersoft.data.entities.pattern;

import java.util.Objects;
import java.util.Set;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.Recipe;
import jakarta.persistence.*;

@Entity
@Table(name = "pattern")
public class Pattern extends AbstractEntity
{
	private String name;
	private String description ="";
	private Character religious ='N';

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
		Pattern pattern = (Pattern) o;
		return Objects.equals(name, pattern.name) && Objects.equals(description, pattern.description) && Objects.equals(religious, pattern.religious);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), name, description, religious);
	}

	@Override
	public String toString()
	{
		return "Pattern{" +
				"religious=" + religious +
				", description='" + description + '\'' +
				", name='" + name + '\'' +
				'}';
	}

	public boolean equalsDefault(DefaultPattern defaultPattern)
	{
		return (name == null || defaultPattern == null) ? false : name.equals(defaultPattern.getName());
	}
}
