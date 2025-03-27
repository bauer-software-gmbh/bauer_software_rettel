package de.bauersoft.data.repositories.tour;

import de.bauersoft.data.entities.tour.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle>
{
    @Query("""
        SELECT vehicle FROM Vehicle vehicle
        WHERE NOT EXISTS (
            SELECT 1 FROM Tour tour 
            WHERE tour.vehicle = vehicle
            AND tour.holidayMode = :holidayMode
        )
    """)
    List<Vehicle> findAllUnplannedVehicles(boolean holidayMode);
}
