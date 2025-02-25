package de.bauersoft.data.repositories.institutionfields;

import de.bauersoft.data.entities.institutionField.InstitutionField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InstitutionFieldsRepository extends JpaRepository<InstitutionField, Long>, JpaSpecificationExecutor<InstitutionField>
{

}
