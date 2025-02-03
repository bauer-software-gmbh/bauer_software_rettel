package de.bauersoft.data.repositories.unit;

import de.bauersoft.data.entities.unit.Unit;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class UnitGridDataRepository extends AbstractGridDataRepository<Unit>
{
    public UnitGridDataRepository()
    {
        super(Unit.class);
    }
}
