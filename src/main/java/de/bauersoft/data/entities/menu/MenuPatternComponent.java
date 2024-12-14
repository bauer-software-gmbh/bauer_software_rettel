package de.bauersoft.data.entities.menu;


import de.bauersoft.data.entities.Component;
import de.bauersoft.data.entities.pattern.Pattern;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "menu_pattern_components")
public class MenuPatternComponent
{
    //TODO ohne insertable und ohne updatable = false -> fehler -> Sven Fragen :)

    @EmbeddedId
    private MenuPatternComponentId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_id", insertable = false, updatable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pattern_id", insertable = false, updatable = false)
    private Pattern pattern;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "menu_pattern_components",
            joinColumns = {@JoinColumn(name = "menu_id"), @JoinColumn(name = "pattern_id")}
    )
    private List<Component> components;

    public Menu getMenu()
    {
        return menu;
    }

    public void setMenu(Menu menu)
    {
        this.menu = menu;
    }

    public Pattern getPattern()
    {
        return pattern;
    }

    public void setPattern(Pattern pattern)
    {
        this.pattern = pattern;
    }

    public List<Component> getComponent()
    {
        return components;
    }

    public MenuPatternComponent setComponent(List<Component> components)
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
        MenuPatternComponent that = (MenuPatternComponent) o;
        return Objects.equals(menu, that.menu) && Objects.equals(pattern, that.pattern) && Objects.equals(components, that.components);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(menu, pattern, components);
    }

    @Override
    public String toString()
    {
        return "MenuPatternComponent{" +
                "id=" + id +
                ", menu=" + menu +
                ", pattern=" + pattern +
                ", components=" + components +
                '}';
    }
}
