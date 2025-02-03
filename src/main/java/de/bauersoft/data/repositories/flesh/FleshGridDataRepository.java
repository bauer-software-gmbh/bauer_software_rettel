package de.bauersoft.data.repositories.flesh;

import de.bauersoft.data.entities.flesh.Flesh;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class FleshGridDataRepository extends AbstractGridDataRepository<Flesh>
{
    public FleshGridDataRepository()
    {
        super(Flesh.class);
    }
}
