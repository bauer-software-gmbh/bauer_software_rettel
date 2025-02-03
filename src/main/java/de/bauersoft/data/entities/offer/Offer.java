package de.bauersoft.data.entities.offer;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.menu.Menu;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "offer", uniqueConstraints =
        {
                @UniqueConstraint(columnNames = {"local_date", "field_id"})
        })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Offer extends AbstractEntity
{

    @Column(nullable = false)
    private LocalDate localDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Field field;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "offer_menu",
            joinColumns = @JoinColumn(name = "offer_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_id"))
    private Set<Menu> menus;

}
