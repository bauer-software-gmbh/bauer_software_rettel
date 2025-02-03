package de.bauersoft.data.repositories.address;

import de.bauersoft.data.entities.address.Address;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class AddressGridDataRepository extends AbstractGridDataRepository<Address>
{
        public AddressGridDataRepository()
        {
            super(Address.class);
        }
}
