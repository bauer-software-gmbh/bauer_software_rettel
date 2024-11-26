package de.bauersoft.data.repositories.field;

import org.springframework.stereotype.Service;

import de.bauersoft.data.entities.Field;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;

@Service
public class FieldGridDataRepository extends AbstractGridDataRepository<Field>{

	public FieldGridDataRepository() {
		super(Field.class);
	}

}
