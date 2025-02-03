package de.bauersoft.data.repositories.field;

import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class FieldGridDataRepository extends AbstractGridDataRepository<Field>
{
    public FieldGridDataRepository()
    {
        super(Field.class);
    }
}
