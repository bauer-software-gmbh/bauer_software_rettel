package de.bauersoft.data.entities.formulation;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FormulationKey implements Serializable
{
    @Column(nullable = false)
    private Long recipeId;

    @Column(nullable = false)
    private Long ingredientId;

    @Override
    public int hashCode()
    {
        return Objects.hash(recipeId, ingredientId);
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null || getClass() != o.getClass())
            return false;

        FormulationKey that = (FormulationKey) o;
        return Objects.equals(recipeId, that.recipeId) && Objects.equals(ingredientId, that.ingredientId);
    }

    @Override
    public String toString()
    {
        return "FormulationKey{" +
                "recipeId=" + recipeId +
                ", ingredientId=" + ingredientId +
                '}';
    }
}
