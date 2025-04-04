package de.bauersoft.data.entities.order;

import de.bauersoft.components.container.ContainerID;
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
public class Order extends AbstractEntity implements ContainerID<Long>
{
    @Transient
    private boolean confirmed = false;

    @Transient
    private boolean hook = false;

    @Column(nullable = false)
    private LocalDate orderDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Institution institution;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Field field;

    @Column(nullable = false, columnDefinition = "BOOLEAN default false")
    private boolean customerOrdered = false;

    @OneToMany(mappedBy = "_order", fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE})
    private Set<OrderData> orderData = new HashSet<>();

    @OneToMany(mappedBy = "_order", fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE})
    private Set<OrderAllergen> orderAllergens = new HashSet<>();

    @Override
    public String toString()
    {
        return "Order{" +
                "id=" + getId() +
                ", orderDate=" + orderDate +
                ", institution=" + institution +
                ", field=" + field +
                ", orderData=" + orderData +
                ", orderAllergens=" + orderAllergens +
                '}';
    }
}