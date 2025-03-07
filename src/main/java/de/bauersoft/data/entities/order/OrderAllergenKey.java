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
public class OrderAllergenKey implements Serializable
{
    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long allergenId;

    @Override
    public boolean equals(Object o)
    {
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        OrderAllergenKey that = (OrderAllergenKey) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(allergenId, that.allergenId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(orderId, allergenId);
    }

    @Override
    public String toString()
    {
        return "OrderAllergenKey{" +
                "orderId=" + orderId +
                ", allergenId=" + allergenId +
                '}';
    }
}
