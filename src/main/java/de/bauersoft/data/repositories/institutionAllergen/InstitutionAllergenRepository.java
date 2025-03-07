package de.bauersoft.data.repositories.institutionAllergen;

import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface InstitutionAllergenRepository extends JpaRepository<InstitutionAllergen, Long>, JpaSpecificationExecutor<InstitutionAllergen>
{
    List<InstitutionAllergen> findAllByInstitutionField(InstitutionField institutionField);
}
