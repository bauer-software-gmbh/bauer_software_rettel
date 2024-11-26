package de.bauersoft.data.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "formulation")
public class Formulation {
	@EmbeddedId
	private FormulationKey id = new FormulationKey();
	
	@ManyToOne(targetEntity = Ingredient.class, fetch = FetchType.EAGER,cascade = CascadeType.PERSIST)
	@MapsId("ingredientId")
	private Ingredient ingredient;
	
	private float quantity;

	public FormulationKey getId() {
		return id;
	}

	public void setId(FormulationKey id) {
		this.id = id;
	}

	public Ingredient getIngredient() {
		return ingredient;
	}

	public void setIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
	}

	public float getQuantity() {
		return quantity;
	}

	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}

	
}
