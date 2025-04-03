package de.bauersoft.data.entities.variant;

import de.bauersoft.components.container.ContainerID;
import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.component.Component;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.entities.menu.Menu;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "variant")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Variant extends AbstractEntity implements ContainerID<Long>
{
    @Column(length = 10240)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pattern_id", referencedColumnName = "id", nullable = false)
    private Pattern pattern;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "menu_variants",
            joinColumns = @JoinColumn(name = "variant_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "menu_id", referencedColumnName = "id", nullable = false))
    private Menu menu;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "variant_components",
            joinColumns = @JoinColumn(name = "variant_id"),
            inverseJoinColumns = @JoinColumn(name = "component_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Component> components = new HashSet<>();

    @Override
    public String toString()
    {
        return "Variant{" +
                "description='" + description + '\'' +
                ", pattern=" + pattern +
                ", menu=" + menu.getId() +
                ", components=" + components +
                '}';
    }
}
