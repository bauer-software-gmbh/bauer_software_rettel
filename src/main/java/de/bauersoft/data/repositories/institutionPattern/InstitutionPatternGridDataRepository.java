package de.bauersoft.data.repositories.institutionPattern;

import de.bauersoft.data.entities.institution.InstitutionPattern;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class InstitutionPatternGridDataRepository extends AbstractGridDataRepository<InstitutionPattern>
{
    public InstitutionPatternGridDataRepository()
    {
        super(InstitutionPattern.class);
    }
}
