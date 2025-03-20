package de.bauersoft.data.repositories.tourPlanning;

import de.bauersoft.data.entities.tourPlanning.vehicle.VehicleDowntime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface VehicleDowntimeRepository extends JpaRepository<VehicleDowntime, Long>, JpaSpecificationExecutor<VehicleDowntime>
{
    List<VehicleDowntime> findAllByVehicle_Id(Long vehicleId);
}
