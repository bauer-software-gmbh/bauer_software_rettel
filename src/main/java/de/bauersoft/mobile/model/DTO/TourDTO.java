package de.bauersoft.mobile.model.DTO;

import de.bauersoft.data.entities.address.Address;
import de.bauersoft.data.entities.order.Order;
import de.bauersoft.data.entities.tour.driver.Driver;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.tour.tour.Tour;
import de.bauersoft.data.entities.tour.tour.TourEntry;
import de.bauersoft.data.entities.tour.tour.TourInstitution;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private String bemerkung; // Nur eine Notiz pro Tour/Tag

    public TourDTO(Tour tour, Driver driver, Driver coDriver, List<Institution> institutions, List<Address> addresses, List<Order> orders, Map<Long, TourInstitution> institutionToTIMap, TourEntry tourEntry) {
        this.id = tour.getId();
        this.name = tour.getName();
        this.startDateTime = tour.getStartDateTime();
        this.endDateTime = tour.getEndDateTime();
        this.vehicle = (tour.getVehicle() != null) ? new VehicleDTO(tour.getVehicle()) : null;
        this.driver = (driver != null) ? new DriverDTO(driver, tour.getDrivesUntil()) : null;
        this.coDriver = (coDriver != null) ? new DriverDTO(coDriver, tour.getCoDrivesUntil()) : null;

        this.institutions = institutions.stream()
                .map(i -> new InstitutionDTO(i, orders, institutionToTIMap.get(i.getId())))
                .sorted(Comparator.comparing(
                        InstitutionDTO::getExpectedArrivalTime,
                        Comparator.nullsLast(LocalTime::compareTo) // falls eine Zeit null ist
                ))
                .collect(Collectors.toList());



        this.bemerkung = tourEntry.getTimeWindow() + " " + tourEntry.getNote();
    }
}
