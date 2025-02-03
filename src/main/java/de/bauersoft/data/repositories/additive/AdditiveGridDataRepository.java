package de.bauersoft.data.repositories.additive;

import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class AdditiveGridDataRepository extends AbstractGridDataRepository<Additive>
{
        public AdditiveGridDataRepository()
        {
            super(Additive.class);
        }
}
