package de.bauersoft.data.repositories.unit;

import org.springframework.stereotype.Service;

import de.bauersoft.data.entities.Unit;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;

@Service
public class UnitGridDataRepository  extends AbstractGridDataRepository<Unit> {

	public UnitGridDataRepository() {
		super(Unit.class);
	}

}
