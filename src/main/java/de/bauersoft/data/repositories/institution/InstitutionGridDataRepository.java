package de.bauersoft.data.repositories.institution;

import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class InstitutionGridDataRepository extends AbstractGridDataRepository<Institution>
{
    public InstitutionGridDataRepository()
    {
        super(Institution.class);
    }
}
