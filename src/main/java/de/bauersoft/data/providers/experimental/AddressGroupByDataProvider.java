package de.bauersoft.data.providers.experimental;

import de.bauersoft.data.entities.address.Address;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class AddressGroupByDataProvider extends GroupByDataProvider<Address> {

	public AddressGroupByDataProvider(AbstractGridDataRepository<Address> repository) {
		super(repository, Address.class);
	}
}
