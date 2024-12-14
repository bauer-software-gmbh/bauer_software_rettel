package de.bauersoft.data.entities.menu;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class MenuPatternComponentsId
{

    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "pattern_id", nullable = true)
    private Long patternId;

    public Long getMenuId()
    {
        return menuId;
    }

    public MenuPatternComponentsId setMenuId(Long menuId)
    {
        this.menuId = menuId;
        return this;
    }

    public Long getPatternId()
    {
        return patternId;
    }

    public MenuPatternComponentsId setPatternId(Long patternId)
    {
        this.patternId = patternId;
        return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        MenuPatternComponentsId that = (MenuPatternComponentsId) o;
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
        return "MenuPatternComponentsId{" +
                "menuId=" + menuId +
                ", patternId=" + patternId +
                '}';
    }
}
