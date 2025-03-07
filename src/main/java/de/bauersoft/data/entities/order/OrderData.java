package de.bauersoft.data.entities.order;

import de.bauersoft.data.entities.variant.Variant;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "order_data")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderData
{
    @EmbeddedId
    private OrderDataKey id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("orderId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Order _order;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("variantId")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private Variant variant;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer amount = 0;

    @Override
    public String toString()
    {
        return "OrderData{" +
                "id=" + id +
                ", _order=" + _order.getId() +
                ", variant=" + variant +
                ", amount=" + amount +
                '}';
    }
}
