package de.bauersoft.data.repositories.institution;

import org.springframework.stereotype.Service;

import de.bauersoft.data.entities.Institution;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;

@Service
public class InstitutionGridDataRepository extends AbstractGridDataRepository<Institution>{

	public InstitutionGridDataRepository() {
		super(Institution.class);
	}
}
