package de.bauersoft.data.entities.formulation;

import de.bauersoft.components.container.ContainerID;
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
//Fragile angelegenheit aber works lol
public class Formulation implements ContainerID<FormulationKey>
{
    @EmbeddedId
    private FormulationKey id;

    @Column(nullable = false, columnDefinition = "float default 1.0")
    private double quantity = 1f;

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
