package de.bauersoft.data.repositories.institutionMultiplier;

import de.bauersoft.data.entities.institution.InstitutionMultiplier;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class InstitutionMultiplierGridDataRepository extends AbstractGridDataRepository<InstitutionMultiplier>
{
    public InstitutionMultiplierGridDataRepository()
    {
        super(InstitutionMultiplier.class);
    }
}
