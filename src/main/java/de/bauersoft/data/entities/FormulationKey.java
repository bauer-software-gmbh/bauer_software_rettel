package de.bauersoft.data.entities;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class FormulationKey implements Serializable{

	@Column(name = "recipe_id")
    Long recipeId;

    @Column(name = "ingredient_id")
    Long ingredientId;

	public Long getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(Long recipeId) {
		this.recipeId = recipeId;
	}

	public Long getIngredientId() {
		return ingredientId;
	}

	public void setIngredientId(Long ingredientId) {
		this.ingredientId = ingredientId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(ingredientId, recipeId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormulationKey other = (FormulationKey) obj;
		return Objects.equals(ingredientId, other.ingredientId) && Objects.equals(recipeId, other.recipeId);
	}
}
