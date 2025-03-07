package de.bauersoft.data.entities.order;

import de.bauersoft.data.entities.allergen.Allergen;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "order_allergen")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderAllergen
{
    @EmbeddedId
    private OrderAllergenKey id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("orderId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Order _order;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("allergenId")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private Allergen allergen;

    @Column(nullable = false)
    private int amount;

    @Override
    public String toString()
    {
        return "OrderAllergen{" +
                "id=" + id +
                ", _order=" + _order.getId() +
                ", allergen=" + allergen +
                ", amount=" + amount +
                '}';
    }
}
