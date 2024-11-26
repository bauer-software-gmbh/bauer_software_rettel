package de.bauersoft.data.repositories.address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.bauersoft.data.entities.Address;

public interface AddressRepository extends JpaRepository<Address, Long>,JpaSpecificationExecutor<Address> {
}
