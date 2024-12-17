package de.bauersoft.data.repositories.day;

import de.bauersoft.data.entities.Day;
import de.bauersoft.data.entities.Week;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class DayGridDataRepository extends AbstractGridDataRepository<Day> {

	public DayGridDataRepository() {
		super(Day.class);
		
	}

}
