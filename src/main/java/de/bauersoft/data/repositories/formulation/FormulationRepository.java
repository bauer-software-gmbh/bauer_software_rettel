package de.bauersoft.data.repositories.formulation;

import de.bauersoft.data.entities.formulation.Formulation;
import de.bauersoft.data.entities.formulation.FormulationKey;
import de.bauersoft.data.entities.recipe.Recipe;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface FormulationRepository extends JpaRepository<Formulation, FormulationKey>, JpaSpecificationExecutor<Formulation>
{

    List<Formulation> findAllByRecipeId(Long recipeId);

    @Transactional
    @Modifying
    @Query("delete from Formulation f where f.id.recipeId = :recipeId")
    void deleteAllByRecipeId(Long recipeId);

//    @Query("select f from Formulation f where f.id.recipeId = :id")
//    public Set<Formulation> findAllByRecipeId(@Param("id") Long recipeId);
//    public List<Formulation> findAllByIdRecipeId(Long recipeId);

//    @Modifying
//    @Transactional
//    @Query("delete from Formulation f where f.id.recipeId = :id")
//    public void deleteAllByRecipeId(@Param("id") Long id);
}
