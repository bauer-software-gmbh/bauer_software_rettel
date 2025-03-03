package de.bauersoft.data.entities.allergen;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.ingredient.Ingredient;
import de.bauersoft.data.entities.order.OrderAllergen;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "allergen")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Allergen extends AbstractEntity
{
    @Column(nullable = false, unique = true, length = 64)
    private String name;

    @Column(length = 1024)
    private String description;

    @ManyToMany(mappedBy = "allergens", fetch = FetchType.EAGER)
    private Set<Ingredient> ingredients = new HashSet<>();

    @OneToMany(mappedBy = "allergen", fetch = FetchType.EAGER)
    private Set<OrderAllergen> orderAllergens = new HashSet<>();
}
