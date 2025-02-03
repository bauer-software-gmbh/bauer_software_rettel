package de.bauersoft.data.entities.formulation;

import de.bauersoft.data.entities.ingredient.Ingredient;
import de.bauersoft.data.entities.recipe.Recipe;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

@Entity
@Table(name = "formulation")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
//Fragile angelegenheit aber works lol
public class Formulation
{
    @EmbeddedId
    private FormulationKey id;

    @Column(nullable = false, columnDefinition = "float default 1.0")
    private float quantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Override
    public String toString()
    {
        return "Formulation{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", recipe=" + recipe +
                ", ingredient=" + ingredient +
                '}';
    }
}
