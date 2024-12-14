package de.bauersoft.data.entities.menu;

import de.bauersoft.data.entities.Component;
import de.bauersoft.data.entities.pattern.Pattern;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "menu_pattern_components")
public class MenuPatternComponents
{
    @EmbeddedId
    private MenuPatternComponentsId id;

    @ManyToOne
    @MapsId("menuId")
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @ManyToOne(optional = true)
    @MapsId("patternId")
    @JoinColumn(name = "pattern_id", nullable = true)
    private Pattern pattern;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "menu_pattern_components",
            joinColumns = {@JoinColumn(name = "menu_id"), @JoinColumn(name = "pattern_id")},
            inverseJoinColumns = {@JoinColumn(name = "component_id")}
    )
    private Set<Component> components;

    public MenuPatternComponentsId getId()
    {
        return id;
    }

    public MenuPatternComponents setId(MenuPatternComponentsId id)
    {
        this.id = id;
        return this;
    }

    public Menu getMenu()
    {
        return menu;
    }

    public MenuPatternComponents setMenu(Menu menu)
    {
        this.menu = menu;
        return this;
    }

    public Pattern getPattern()
    {
        return pattern;
    }

    public MenuPatternComponents setPattern(Pattern pattern)
    {
        this.pattern = pattern;
        return this;
    }

    public Set<Component> getComponents()
    {
        return components;
    }

    public MenuPatternComponents setComponents(Set<Component> components)
    {
        this.components = components;
        return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        MenuPatternComponents that = (MenuPatternComponents) o;
        return Objects.equals(id, that.id) && Objects.equals(components, that.components);
    }

    @Override
    public String toString()
    {
        return "MenuPatternComponents{" +
                "id=" + id +
                ", components=" + components +
                '}';
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, components);
    }
}
