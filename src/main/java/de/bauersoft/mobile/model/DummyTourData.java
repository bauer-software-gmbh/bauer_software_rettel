package de.bauersoft.mobile.model;

import de.bauersoft.data.entities.address.Address;
import de.bauersoft.data.entities.driver.Driver;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.role.Role;
import de.bauersoft.data.entities.tour.Tour;
import de.bauersoft.data.entities.tour.TourInstitution;
import de.bauersoft.data.entities.tour.TourInstitutionKey;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.entities.vehicle.Vehicle;
import de.bauersoft.mobile.model.DTO.TourDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DummyTourData {

    public static TourDTO getDummyTourDTO() {
        // Dummy Fahrzeug
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("AB-123-CD");
        vehicle.setTypeDescription("Transporter");

        // Dummy Nutzer für Fahrer und Co-Fahrer
        User driverUser = new User("max.mustermann", "Max", "Mustermann", "max@example.com",
                Set.of(Role.ADMIN), new HashSet<>());  // **Set<> für Institutionen**
        User coDriverUser = new User("erika.musterfrau", "Erika", "Musterfrau", "erika@example.com",
                Set.of(Role.ADMIN), new HashSet<>());

        // Dummy Fahrer & Co-Fahrer
        Driver driver = new Driver(driverUser, new HashSet<>());
        Driver coDriver = new Driver(coDriverUser, new HashSet<>());

        // Dummy Adressen
        Address address1 = new Address("Hauptstraße", "1", "10115", "Berlin", new HashSet<>());
        Address address2 = new Address("Nebenweg", "42", "80331", "München", new HashSet<>());

        // Dummy Institutionen
        Institution institution1 = new Institution(201L, "Institution A", "Beschreibung A",
                "CUST-001", LocalTime.of(8, 0), LocalTime.of(12, 0),
                address1, new HashSet<>(), new HashSet<>(), new HashSet<>());

        Institution institution2 = new Institution(202L, "Institution B", "Beschreibung B",
                "CUST-002", LocalTime.of(9, 0), LocalTime.of(13, 0),
                address2, new HashSet<>(), new HashSet<>(), new HashSet<>());

        // Institutionen zur Adresse hinzufügen
        address1.getInstitutions().add(institution1);
        address2.getInstitutions().add(institution2);

        // TourInstitutionen erstellen
        TourInstitution tourInstitution1 = new TourInstitution(
                new TourInstitutionKey(1L, 201L),
                null, // Tour wird später gesetzt
                institution1,
                LocalDate.now(),
                LocalDate.of(9999, 12, 31),
                LocalTime.of(10, 30),
                new byte[0], // Dummy-Bild für Temperatur
                new byte[0], // Dummy-Signatur
                LocalDateTime.now()
        );

        TourInstitution tourInstitution2 = new TourInstitution(
                new TourInstitutionKey(1L, 202L),
                null, // Tour wird später gesetzt
                institution2,
                LocalDate.now(),
                LocalDate.of(9999, 12, 31),
                LocalTime.of(11, 15),
                new byte[0],
                new byte[0],
                LocalDateTime.now()
        );

        // Dummy Tour erstellen
        Tour tour = new Tour(
                "Test-Tour",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(4),
                vehicle,
                2, // requiredDrivers
                driver,
                LocalDate.now().plusDays(10),
                coDriver,
                LocalDate.now().plusDays(5),
                Set.of(tourInstitution1, tourInstitution2),
                new HashSet<>()
        );

        // Tour in TourInstitution setzen
        tourInstitution1.setTour(tour);
        tourInstitution2.setTour(tour);

        // Fahrer können diese Tour fahren
        driver.getDriveableTours().add(tour);
        coDriver.getDriveableTours().add(tour);

        // **Hier wurde `Set<>` zu `List<>` geändert**
        return new TourDTO(tour, driver, coDriver, List.of(institution1, institution2), List.of(address1, address2));
    }
}
