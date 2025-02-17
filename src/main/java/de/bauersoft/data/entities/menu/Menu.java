package de.bauersoft.data.entities.menu;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.flesh.Flesh;
import de.bauersoft.data.entities.offer.Offer;
import de.bauersoft.data.entities.variant.Variant;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "menu")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Menu extends AbstractEntity
{
    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    private Flesh flesh;

    @OneToMany(mappedBy = "menu", fetch = FetchType.EAGER)
    private Set<Variant> variants;

    @ManyToMany(mappedBy = "menus", fetch = FetchType.EAGER)
    private Set<Offer> offers;

    @Override
    public String toString()
    {
        return "Menu{" +
                "name='" + name + '\'' +
                ", flesh=" + flesh +
                ", variants=" + variants +
                ", offers(amount)=" + offers.size() +
                '}';
    }
}
