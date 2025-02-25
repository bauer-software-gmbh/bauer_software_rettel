package de.bauersoft.data.repositories.institutionPattern;

import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.data.entities.institutionFieldPattern.InstitutionPattern;
import de.bauersoft.data.entities.institutionFieldPattern.InstitutionPatternKey;
import de.bauersoft.data.entities.pattern.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface InstitutionPatternRepository extends JpaRepository<InstitutionPattern, InstitutionPatternKey>, JpaSpecificationExecutor<InstitutionPattern>
{
    Optional<InstitutionPattern> findByInstitutionFieldAndPattern(InstitutionField institutionField, Pattern pattern);
}
