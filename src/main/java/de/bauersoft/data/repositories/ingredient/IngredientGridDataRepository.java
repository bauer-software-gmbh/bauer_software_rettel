package de.bauersoft.data.repositories.ingredient;

import de.bauersoft.data.entities.ingredient.Ingredient;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class IngredientGridDataRepository extends AbstractGridDataRepository<Ingredient>
{
    public IngredientGridDataRepository()
    {
        super(Ingredient.class);
    }
}
