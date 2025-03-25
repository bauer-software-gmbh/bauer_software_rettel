package de.bauersoft.data.providers;

import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.services.AllergenService;
import org.springframework.stereotype.Service;

@Service
public class AllergenDataProvider extends FilterDataProvider<Allergen, Long>
{

	public AllergenDataProvider(AllergenService allergenService)
	{
		super(allergenService);
	}
}
