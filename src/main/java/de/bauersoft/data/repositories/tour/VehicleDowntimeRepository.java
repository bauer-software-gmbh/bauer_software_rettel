package de.bauersoft.data.repositories.tour;

import de.bauersoft.data.entities.tour.vehicle.VehicleDowntime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface VehicleDowntimeRepository extends JpaRepository<VehicleDowntime, Long>, JpaSpecificationExecutor<VehicleDowntime>
{
    List<VehicleDowntime> findAllByVehicle_Id(Long vehicleId);


}
