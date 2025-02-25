package de.bauersoft.data.entities.order;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.Institution;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "_order", uniqueConstraints =
        {
                @UniqueConstraint(columnNames = {"order_date", "institution_id", "field_id"})
        })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Order extends AbstractEntity
{
    @Column(nullable = false)
    private LocalDate localDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private Institution institution;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private Field field;

    @OneToMany(mappedBy = "_order", fetch = FetchType.EAGER)
    private Set<OrderData> orderData = new HashSet<>();

    @OneToMany(mappedBy = "_order", fetch = FetchType.EAGER)
    private Set<OrderAllergen> orderAllergens = new HashSet<>();

    @Override
    public String toString()
    {
        return "Order{" +
                "orderDate=" + localDate +
                ", institution=" + institution +
                ", field=" + field +
                ", orderData=" + orderData +
                ", orderAllergens=" + orderAllergens +
                '}';
    }
}
