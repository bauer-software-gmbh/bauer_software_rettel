package de.bauersoft.data.entities.menu;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.flesh.Flesh;
import de.bauersoft.data.entities.offer.Offer;
import de.bauersoft.data.entities.variant.Variant;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "menu")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Menu extends AbstractEntity
{
    @Column(nullable = false, unique = true, length = 64)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    private Flesh flesh;

    @OneToMany(mappedBy = "menu", fetch = FetchType.EAGER)
    private Set<Variant> variants = new HashSet<>();

    @Transient
    private boolean confirmed = false;

    @Override
    public String toString()
    {
        return "Menu{" +
                "name='" + name + '\'' +
                ", flesh=" + flesh +
                ", variants=" + variants +
                '}';
    }
}
