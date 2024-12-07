package de.bauersoft.data.repositories.pattern;

import org.springframework.stereotype.Service;

import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;

@Service
public class PatternGridDataRepository extends AbstractGridDataRepository<Pattern> {

	public PatternGridDataRepository() {
		super(Pattern.class);
	}
}
