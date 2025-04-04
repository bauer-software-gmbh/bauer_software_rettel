package de.bauersoft.data.entities.unit;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.ingredient.Ingredient;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Set;

@Entity
@Table(name = "unit")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Unit extends AbstractEntity
{
    @Column(nullable = false, unique = true, length = 64)
    private String name;

    @Column(nullable = false, unique = true, length = 8)
    private String shorthand;

    @Column(columnDefinition = "float default 0.0")
    private Double parentFactor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_unit_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Unit parentUnit;

    @OneToMany(mappedBy = "unit", fetch = FetchType.EAGER)
    private Set<Ingredient> ingredients;

    public Unit(String name, String shorthand)
    {
        this.name = name;
        this.shorthand = shorthand;
    }
}
