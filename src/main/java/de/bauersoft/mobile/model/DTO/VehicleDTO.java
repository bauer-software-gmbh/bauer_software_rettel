package de.bauersoft.mobile.model.DTO;

import de.bauersoft.data.entities.tour.vehicle.Vehicle;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleDTO {
    private Long id;
    private String licensePlate;
    private String typeDescription;

    public VehicleDTO(Vehicle vehicle) {
        this.id = vehicle.getId();
        this.licensePlate = vehicle.getLicensePlate();
        this.typeDescription = vehicle.getTypeDescription();
    }
}

