package de.bauersoft.data.repositories.recipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.bauersoft.data.entities.Recipe;


public interface RecipeRepository  extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {

}
