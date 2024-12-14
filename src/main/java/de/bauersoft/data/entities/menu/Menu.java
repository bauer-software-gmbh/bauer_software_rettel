package de.bauersoft.data.entities.menu;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.pattern.Pattern;
import jakarta.persistence.*;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "menu")
public class Menu extends AbstractEntity
{
	private String name;
	private String description;

	@OneToMany(mappedBy = "menu", fetch = FetchType.EAGER)
	@MapKey(name = "pattern")
	private Map<Pattern, MenuPatternComponents> menuPatternComponents;

	//	@OneToMany(mappedBy = "id.menuId", fetch = FetchType.EAGER)
//	@MapKey(name ="id.pattern_id")
//	private Map<Pattern, MenuPatternComponents> patternComponentsMap;

//	@OneToMany(mappedBy = "id", fetch = FetchType.EAGER)
//	@MapKeyColumn(name  = "patern_id")
//	private Map<Pattern, MenuPatternComponents> components;

//	@OneToMany(mappedBy = "menu", fetch = FetchType.EAGER)
//	@MapKey(name = "pattern")
//	private Map<Pattern, MenuPatternComponent> components;

//	@ElementCollection(fetch = FetchType.EAGER)
//	@CollectionTable(
//			name = "menu_pattern_components",
//			joinColumns = @JoinColumn(name = "menu_id")
//	)
//	@MapKeyColumn(name = "pattern_id")
//	@Column(name = "component_id")
//	@CollectionOfElements
//	private Map<Pattern, Set<Component>> components;

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

	public Optional<Map<Pattern, MenuPatternComponents>> getMenuPatternComponents()
	{
		return Optional.ofNullable(menuPatternComponents);
	}

	public Menu setMenuPatternComponents(Map<Pattern, MenuPatternComponents> menuPatternComponents)
	{
		this.menuPatternComponents = menuPatternComponents;
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
		Menu menu = (Menu) o;
		return Objects.equals(name, menu.name) && Objects.equals(description, menu.description) && Objects.equals(menuPatternComponents, menu.menuPatternComponents);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), name, description, menuPatternComponents);
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
