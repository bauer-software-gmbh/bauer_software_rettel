package de.bauersoft.data.repositories.recipe;

import de.bauersoft.data.entities.recipe.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe>
{
    boolean existsByPatternsId(Long id);
}
