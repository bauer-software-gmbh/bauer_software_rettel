package de.bauersoft.data.entities.order;

import de.bauersoft.components.container.ContainerID;
import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.allergen.Allergen;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "order_allergen")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderAllergen extends AbstractEntity implements ContainerID<Long>
{
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private Order _order;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "order_allergens",
            joinColumns = @JoinColumn(name = "order_allergen_id"),
            inverseJoinColumns = @JoinColumn(name = "allergen_id"))
    private Set<Allergen> allergens = new HashSet<>();

    @Override
    public String toString()
    {
        return "OrderAllergen{" +
                "allergens=" + allergens +
                '}';
    }
}
