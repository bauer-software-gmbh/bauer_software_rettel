package de.bauersoft.mobile.model.DTO;

import de.bauersoft.data.entities.address.Address;
import de.bauersoft.data.entities.tourPlanning.driver.Driver;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.tourPlanning.tour.Tour;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class TourDTO {
    private Long id;
    private String name;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private VehicleDTO vehicle;
    private DriverDTO driver;
    private DriverDTO coDriver;
    private List<InstitutionDTO> institutions;
    private List<AddressDTO> addresses;

    public TourDTO(Tour tour, Driver driver, Driver coDriver, List<Institution> institutions, List<Address> addresses) {
        this.id = tour.getId();
        this.name = tour.getName();
        this.startDateTime = tour.getStartDateTime();
        this.endDateTime = tour.getEndDateTime();
        this.vehicle = (tour.getVehicle() != null) ? new VehicleDTO(tour.getVehicle()) : null;
        this.driver = (driver != null) ? new DriverDTO(driver, tour.getDrivesUntil()) : null;
        this.coDriver = (coDriver != null) ? new DriverDTO(coDriver, tour.getCoDrivesUntil()) : null;

        this.institutions = (institutions != null && !institutions.isEmpty())
                ? institutions.stream().map(InstitutionDTO::new).collect(Collectors.toList())
                : List.of();
        this.addresses = (addresses != null) ? addresses.stream().map(AddressDTO::new).collect(Collectors.toList()) : List.of();
    }
}
