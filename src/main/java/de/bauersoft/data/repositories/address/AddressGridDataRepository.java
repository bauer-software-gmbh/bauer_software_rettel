package de.bauersoft.data.repositories.address;

import org.springframework.stereotype.Service;

import de.bauersoft.data.entities.Address;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;

@Service
public class AddressGridDataRepository  extends AbstractGridDataRepository<Address>{

	public AddressGridDataRepository() {
		super(Address.class);
	}

	
}
