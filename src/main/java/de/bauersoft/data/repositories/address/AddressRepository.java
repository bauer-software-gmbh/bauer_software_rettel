package de.bauersoft.data.repositories.address;

import de.bauersoft.data.entities.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AddressRepository extends JpaRepository<Address, Long>, JpaSpecificationExecutor<Address>
{
}
