package de.bauersoft.data.repositories.ingredient;

import de.bauersoft.data.entities.ingredient.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long>, JpaSpecificationExecutor<Ingredient>
{
    boolean existsByUnitId(Long id);

    boolean existsByAdditivesId(Long id);

    boolean existsByAllergensId(Long id);
}
