package de.bauersoft.data.entities.temp;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.Component;
import de.bauersoft.data.entities.pattern.Pattern;
import jakarta.persistence.*;

@Entity
@Table(name = "part")
public class Part extends AbstractEntity
{

    @OneToOne
    @JoinColumn(name = "pattern_id")
    private Pattern pattern;

    @OneToOne
    @JoinColumn(name = "component_id")
    private Component component;

    public Pattern getPattern()
    {
        return pattern;
    }

    public Part setPattern(Pattern pattern)
    {
        this.pattern = pattern;
        return this;
    }

    public Component getComponent()
    {
        return component;
    }

    public Part setComponent(Component component)
    {
        this.component = component;
        return this;
    }
}
