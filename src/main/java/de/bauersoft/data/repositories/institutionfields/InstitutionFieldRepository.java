package de.bauersoft.data.repositories.institutionfields;

import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface InstitutionFieldRepository extends JpaRepository<InstitutionField, Long>, JpaSpecificationExecutor<InstitutionField>
{
    Optional<InstitutionField> findByInstitutionAndField(Institution institution, Field field);

    List<InstitutionField> findAllByInstitution_Id(Long institutionId);
}
