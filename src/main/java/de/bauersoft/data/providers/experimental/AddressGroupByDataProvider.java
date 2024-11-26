package de.bauersoft.data.providers.experimental;

import org.springframework.stereotype.Service;

import de.bauersoft.data.entities.Address;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;

@Service
public class AddressGroupByDataProvider extends GroupByDataProvider<Address> {

	public AddressGroupByDataProvider(AbstractGridDataRepository<Address> repository) {
		super(repository, Address.class);
	}
}
