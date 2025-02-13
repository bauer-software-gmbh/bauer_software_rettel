package de.bauersoft.data.repositories.institutionPattern;

import de.bauersoft.data.entities.institution.InstitutionPattern;
import de.bauersoft.data.entities.institution.InstitutionPatternKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InstitutionPatternRepository extends JpaRepository<InstitutionPattern, InstitutionPatternKey>, JpaSpecificationExecutor<InstitutionPattern>
{
}
