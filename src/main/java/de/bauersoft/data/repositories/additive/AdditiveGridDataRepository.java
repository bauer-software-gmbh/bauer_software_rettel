package de.bauersoft.data.repositories.additive;

import org.springframework.stereotype.Service;

import de.bauersoft.data.entities.Additive;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;

@Service
public class AdditiveGridDataRepository extends AbstractGridDataRepository<Additive>{

	public AdditiveGridDataRepository() {
		super(Additive.class);
	}

}
