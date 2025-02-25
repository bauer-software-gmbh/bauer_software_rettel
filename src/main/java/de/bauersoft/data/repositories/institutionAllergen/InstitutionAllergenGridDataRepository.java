package de.bauersoft.data.repositories.institutionAllergen;

import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergen;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class InstitutionAllergenGridDataRepository extends AbstractGridDataRepository<InstitutionAllergen>
{
    public InstitutionAllergenGridDataRepository()
    {
        super(InstitutionAllergen.class);
    }
}
