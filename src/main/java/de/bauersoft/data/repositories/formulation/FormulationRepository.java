package de.bauersoft.data.repositories.formulation;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.bauersoft.data.entities.Formulation;
import de.bauersoft.data.entities.FormulationKey;
import jakarta.transaction.Transactional;


public interface FormulationRepository extends JpaRepository<Formulation, FormulationKey>, JpaSpecificationExecutor<Formulation>{

	@Query("select f from Formulation f where f.id.recipeId = :id") 
	public Set<Formulation> findAllByRecipeId(@Param("id") Long recipeId);

	@Modifying
	@Transactional
	@Query("delete from Formulation f where f.id.recipeId = :id")
	public void deleteAllByRecipeId(@Param("id") Long id);
	
}
