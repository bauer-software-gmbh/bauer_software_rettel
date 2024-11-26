package de.bauersoft.data.repositories.recipe;

import org.springframework.stereotype.Service;

import de.bauersoft.data.entities.Recipe;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;

@Service
public class RecipeGridDataRepository extends AbstractGridDataRepository<Recipe>{

	public RecipeGridDataRepository() {
		super(Recipe.class);
	}

}