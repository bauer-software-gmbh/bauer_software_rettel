package de.bauersoft.data.repositories.week;

import de.bauersoft.data.entities.Week;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class WeekGridDataRepository extends AbstractGridDataRepository<Week> {

	public WeekGridDataRepository() {
		super(Week.class);
		
	}

}
