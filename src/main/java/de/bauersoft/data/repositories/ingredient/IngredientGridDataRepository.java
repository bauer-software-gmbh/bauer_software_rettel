package de.bauersoft.data.repositories.ingredient;

import org.springframework.stereotype.Service;

import de.bauersoft.data.entities.Ingredient;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;

@Service
public class IngredientGridDataRepository extends AbstractGridDataRepository<Ingredient> {

	public IngredientGridDataRepository(){
		super(Ingredient.class);
	}

}