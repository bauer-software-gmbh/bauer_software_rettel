package de.bauersoft.data.repositories.tour;

import de.bauersoft.data.entities.tour.driver.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long>, JpaSpecificationExecutor<Driver>
{
    boolean existsDriverByUser_Id(Long userId);

    Optional<Driver> findById(Long id);

}
