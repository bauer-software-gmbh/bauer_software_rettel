package de.bauersoft.data.repositories.institutionfields;

import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institution.InstitutionField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface InstitutionFieldsRepository extends JpaRepository<InstitutionField, Long>, JpaSpecificationExecutor<InstitutionField>
{
    Optional<InstitutionField> findByInstitutionAndField(Institution institution, Field field);
}
