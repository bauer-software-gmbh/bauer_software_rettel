package de.bauersoft.data.repositories.allergen;

import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class AllergenGridDataRepository extends AbstractGridDataRepository<Allergen>
{
    public AllergenGridDataRepository()
    {
        super(Allergen.class);
    }
}
