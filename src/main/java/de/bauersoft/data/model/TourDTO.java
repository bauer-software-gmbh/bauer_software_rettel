package de.bauersoft.data.model;

import de.bauersoft.data.entities.address.Address;
import de.bauersoft.data.entities.driver.Driver;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.tour.Tour;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TourDTO {
    private Long id;
    private String name;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String vehicleLicensePlate;
    private Long vehicleId;
    private Long driverId;
    private String driverName;
    private Long coDriverId;
    private String coDriverName;
    private LocalDate coDrivesUntil;
    private LocalDate drivesUntil;
    private int requiredDrivers;
    private List<String> institutionNames;
    private List<Long> institutionIds;
    private List<String> addressStreet;
    private List<String> addressNumber;
    private List<String> addressCity;
    private List<String> addressPostal;

    public TourDTO(Tour tour, Driver driver, Driver coDriver, List<Institution> institutions, List<Address> addresses) {
        this.id = tour.getId();
        this.name = tour.getName();
        this.startDateTime = tour.getStartDateTime();
        this.endDateTime = tour.getEndDateTime();
        this.vehicleLicensePlate = (tour.getVehicle() != null) ? tour.getVehicle().getLicensePlate() : "Kein Fahrzeug";
        this.vehicleId = (tour.getVehicle() != null) ? tour.getVehicle().getId() : null;
        this.driverId = (driver != null) ? driver.getId() : null;
        this.driverName = (driver != null && driver.getUser() != null) ? driver.getUser().getName() : "Unbekannter Fahrer";
        this.coDriverId = (coDriver != null) ? coDriver.getId() : null;
        this.coDriverName = (coDriver != null && coDriver.getUser() != null) ? coDriver.getUser().getName() : "Unbekannter Co-Fahrer";
        this.coDrivesUntil = tour.getCoDrivesUntil();
        this.drivesUntil = tour.getDrivesUntil();
        this.requiredDrivers = tour.getRequiredDrivers();

        // Institutionen (Name & ID)
        this.institutionNames = institutions.stream().map(Institution::getName).collect(Collectors.toList());
        this.institutionIds = institutions.stream().map(Institution::getId).collect(Collectors.toList());

        // Adressen (Straße, Nummer, Stadt, PLZ)
        this.addressStreet = addresses.stream().map(Address::getStreet).collect(Collectors.toList());
        this.addressNumber = addresses.stream().map(Address::getNumber).collect(Collectors.toList());
        this.addressCity = addresses.stream().map(Address::getCity).collect(Collectors.toList());
        this.addressPostal = addresses.stream().map(Address::getPostal).collect(Collectors.toList());
    }
}
