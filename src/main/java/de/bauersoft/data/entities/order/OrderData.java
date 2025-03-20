package de.bauersoft.data.entities.order;

import de.bauersoft.components.container.ContainerID;
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
public class OrderData implements ContainerID<OrderDataKey>
{
    @EmbeddedId
    private OrderDataKey id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("orderId")
    private Order _order;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("variantId")
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
