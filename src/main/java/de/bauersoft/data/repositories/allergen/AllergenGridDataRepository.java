package de.bauersoft.data.repositories.allergen;

import org.springframework.stereotype.Service;

import de.bauersoft.data.entities.Allergen;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;

@Service
public class AllergenGridDataRepository extends AbstractGridDataRepository<Allergen> {

	public AllergenGridDataRepository() {
		super(Allergen.class);
	}

}
