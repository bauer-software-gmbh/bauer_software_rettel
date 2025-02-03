package de.bauersoft.data.repositories.recipe;

import de.bauersoft.data.entities.recipe.Recipe;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class RecipeGridDataRepository extends AbstractGridDataRepository<Recipe>
{
    public RecipeGridDataRepository()
    {
        super(Recipe.class);
    }
}
