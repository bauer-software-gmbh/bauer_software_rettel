package de.bauersoft.data.entities.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDataKey implements Serializable
{
    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long variantId;

    @Override
    public boolean equals(Object o)
    {
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        OrderDataKey that = (OrderDataKey) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(variantId, that.variantId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(orderId, variantId);
    }

    @Override
    public String toString()
    {
        return "OrderDataKey{" +
                "orderId=" + orderId +
                ", variantId=" + variantId +
                '}';
    }
}
