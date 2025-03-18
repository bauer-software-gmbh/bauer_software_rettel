package de.bauersoft.data.repositories.driver;

import de.bauersoft.data.entities.driver.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long>, JpaSpecificationExecutor<Driver>
{
    Optional<Driver> findById(Long id);
}
