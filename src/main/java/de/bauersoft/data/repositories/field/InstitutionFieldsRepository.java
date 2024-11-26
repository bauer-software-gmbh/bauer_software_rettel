package de.bauersoft.data.repositories.field;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.bauersoft.data.entities.InstitutionFields;
import de.bauersoft.data.entities.InstitutionFieldsKey;
import jakarta.transaction.Transactional;

public interface InstitutionFieldsRepository extends JpaRepository<InstitutionFields, InstitutionFieldsKey>, JpaSpecificationExecutor<InstitutionFields>{

	@Query("select i from InstitutionFields i where i.id.institutionId = :id") 
	public Set<InstitutionFields> findAllByInstitutionId(@Param("id") Long recipeId);

	@Modifying
	@Transactional
	@Query("delete from InstitutionFields i where i.id.institutionId = :id")
	public void deleteAllByInstitutionId(@Param("id") Long id);
}
