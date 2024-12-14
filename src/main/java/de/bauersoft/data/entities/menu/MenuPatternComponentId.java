package de.bauersoft.data.entities.menu;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MenuPatternComponentId implements Serializable
{
    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "pattern_id")
    private Long patternId;

    @Override
    public boolean equals(Object o)
    {
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        MenuPatternComponentId that = (MenuPatternComponentId) o;
        return Objects.equals(menuId, that.menuId) && Objects.equals(patternId, that.patternId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(menuId, patternId);
    }

    @Override
    public String toString()
    {
        return "MenuPatternComponentId{" +
                "menuId=" + menuId +
                ", patternId=" + patternId +
                '}';
    }
}
