package de.bauersoft.data.repositories.tour;

import de.bauersoft.data.entities.tour.driver.Driver;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.lang.ScopedValue;
import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long>, JpaSpecificationExecutor<Driver>
{
    boolean existsDriverByUser_Id(Long userId);

    Optional<Driver> findById(Long id);

    @Query("""
        SELECT driver FROM Driver driver
        WHERE NOT EXISTS (
            SELECT 1 FROM Tour tour 
            WHERE (tour.driver = driver OR tour.coDriver = driver)
            AND tour.holidayMode = :holidayMode
        )
        AND EXISTS (
            SELECT 1 FROM driver.driveableTours driveableTour 
            WHERE driveableTour.id = :tourId
        )
    """)
    List<Driver> findAllUnplannedAllowedDrivers(Long tourId, boolean holidayMode);

    @Query("""
        SELECT d FROM Driver d 
        LEFT JOIN Tour t ON (t.driver = d OR t.coDriver = d) AND t.holidayMode = :holidayMode
        WHERE t.id IS NULL
    """)
    List<Driver> findAllUnplannedDrivers(boolean holidayMode);

    @Transactional
    @Modifying
    @Query(value = """
        DELETE FROM driveable_tours
        WHERE tour_id = :tourId
    """, nativeQuery = true)
    void deleteAllDriveableToursByTourId(Long tourId);

    Optional<Driver> findByUser_Id(Long userId);
}
