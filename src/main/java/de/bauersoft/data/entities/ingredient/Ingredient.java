package de.bauersoft.data.entities.ingredient;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.formulation.Formulation;
import de.bauersoft.data.entities.unit.Unit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ingredient", indexes =
        {
                @Index(name = "unit_index", columnList = "unit_id", unique = false)
        })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Ingredient extends AbstractEntity
{
    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1024)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_id", referencedColumnName = "id", nullable = false)
    private Unit unit;

    @OneToMany(mappedBy = "ingredient", fetch = FetchType.EAGER)
    private Set<Formulation> formulations = new HashSet<>(); //<- da lassen!

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "allergenic",
            joinColumns = @JoinColumn(name = "ingredient_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "allergen_id", referencedColumnName = "id"))
    private Set<Allergen> allergens;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "additives",
            joinColumns = @JoinColumn(name = "ingredient_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "additive_id", referencedColumnName = "id"))
    private Set<Additive> additives;

    @Override
    public String toString()
    {
        return "Ingredient{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
