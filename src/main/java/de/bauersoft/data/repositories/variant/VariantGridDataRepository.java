package de.bauersoft.data.repositories.variant;

import de.bauersoft.data.entities.variant.Variant;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class VariantGridDataRepository extends AbstractGridDataRepository<Variant>
{
    public VariantGridDataRepository()
    {
        super(Variant.class);
    }
}
