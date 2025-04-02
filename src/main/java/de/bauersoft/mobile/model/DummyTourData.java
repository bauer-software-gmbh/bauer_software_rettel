//package de.bauersoft.mobile.model;
//
//import de.bauersoft.data.entities.address.Address;
//import de.bauersoft.data.entities.order.Order;
//import de.bauersoft.data.entities.order.OrderAllergen;
//import de.bauersoft.data.entities.order.OrderData;
//import de.bauersoft.data.entities.order.OrderDataKey;
//import de.bauersoft.data.entities.pattern.Pattern;
//import de.bauersoft.data.entities.tour.driver.Driver;
//import de.bauersoft.data.entities.institution.Institution;
//import de.bauersoft.data.entities.role.Role;
//import de.bauersoft.data.entities.tour.tour.Tour;
//import de.bauersoft.data.entities.tour.tour.TourInstitution;
//import de.bauersoft.data.entities.tour.tour.TourInstitutionKey;
//import de.bauersoft.data.entities.user.User;
//import de.bauersoft.data.entities.tour.vehicle.Vehicle;
//import de.bauersoft.data.entities.variant.Variant;
//import de.bauersoft.mobile.model.DTO.TourDTO;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class DummyTourData {
//
//    public static TourDTO getDummyTourDTO() {
//        // Dummy Fahrzeug
//        Vehicle vehicle = new Vehicle();
//        vehicle.setLicensePlate("AB-123-CD");
//        vehicle.setTypeDescription("Transporter");
//
//        // Dummy Nutzer für Fahrer und Co-Fahrer
//        User driverUser = new User("max.mustermann", "Max", "Mustermann", "max@example.com",
//                Set.of(Role.ADMIN), new HashSet<>());  // **Set<> für Institutionen**
//        User coDriverUser = new User("erika.musterfrau", "Erika", "Musterfrau", "erika@example.com",
//                Set.of(Role.ADMIN), new HashSet<>());
//
//        // Dummy Fahrer & Co-Fahrer
//        Driver driver = new Driver(driverUser, new HashSet<>());
//        Driver coDriver = new Driver(coDriverUser, new HashSet<>());
//
//        // Dummy Adressen
//        Address address1 = new Address("Hauptstraße", "1", "10115", "Berlin", new HashSet<>());
//        Address address2 = new Address("Nebenweg", "42", "80331", "München", new HashSet<>());
//
//        // Dummy Institutionen
//        Institution institution1 = new Institution(201L, "Institution A", "Beschreibung A",
//                "CUST-001", LocalTime.of(8, 0), LocalTime.of(12, 0),
//                address1, new HashSet<>(), new HashSet<>(), new HashSet<>());
//
//        Institution institution2 = new Institution(202L, "Institution B", "Beschreibung B",
//                "CUST-002", LocalTime.of(9, 0), LocalTime.of(13, 0),
//                address2, new HashSet<>(), new HashSet<>(), new HashSet<>());
//
//        // Institutionen zur Adresse hinzufügen
//        address1.getInstitutions().add(institution1);
//        address2.getInstitutions().add(institution2);
//
//        // TourInstitutionen erstellen
//        TourInstitution tourInstitution1 = new TourInstitution(
//                new TourInstitutionKey(1L, 201L),
//                null, // Tour wird später gesetzt
//                institution1,
//                LocalTime.of(10, 30),
//                new byte[0], // Dummy-Bild für Temperatur
//                new byte[0], // Dummy-Signatur
//                LocalDateTime.now()
//        );
//
//        TourInstitution tourInstitution2 = new TourInstitution(
//                new TourInstitutionKey(1L, 202L),
//                null, // Tour wird später gesetzt
//                institution2,
//                LocalTime.of(11, 15),
//                new byte[0],
//                new byte[0],
//                LocalDateTime.now()
//        );
//
//        // Dummy Tour erstellen
//        Tour tour = new Tour(
//                "Test-Tour",
//                LocalDateTime.now(),
//                LocalDateTime.now().plusHours(4),
//                vehicle,
//                2, // requiredDrivers
//                driver,
//                LocalDate.now().plusDays(10),
//                coDriver,
//                LocalDate.now().plusDays(5),
//                Set.of(tourInstitution1, tourInstitution2),
//                new HashSet<>()
//        );
//
//        // Tour in TourInstitution setzen
//        tourInstitution1.setTour(tour);
//        tourInstitution2.setTour(tour);
//
//        // Fahrer können diese Tour fahren
//        driver.getDriveableTours().add(tour);
//        coDriver.getDriveableTours().add(tour);
//
//        // Dummy Pattern für Mahlzeit
//        Pattern pattern1 = new Pattern();
//        pattern1.setName("Spaghetti Bolognese");
//
//        Pattern pattern2 = new Pattern();
//        pattern2.setName("Vegetarisches Curry");
//
//        // Variants (Gericht-Varianten)
//        Variant variant1 = new Variant();
//        variant1.setPattern(pattern1);
//
//        Variant variant2 = new Variant();
//        variant2.setPattern(pattern2);
//
//        // Dummy Orders
//        Order order1 = new Order();
//        order1.setInstitution(institution1);
//        order1.setOrderDate(LocalDate.now());
//
//        Order order2 = new Order();
//        order2.setInstitution(institution2);
//        order2.setOrderDate(LocalDate.now());
//
//        // Dummy OrderDataKey
//        OrderDataKey key1 = new OrderDataKey(1L, 1L);
//        OrderDataKey key2 = new OrderDataKey(2L, 2L);
//
//        // OrderData mit Mengen
//        OrderData data1 = new OrderData(key1, order1, variant1, 12);
//        OrderData data2 = new OrderData(key2, order2, variant2, 8);
//
//        // OrderAllergen als leer (du kannst später Dummy-Allergene dazufügen)
//        OrderAllergen allergen1 = new OrderAllergen();
//        allergen1.set_order(order1);
//
//        OrderAllergen allergen2 = new OrderAllergen();
//        allergen2.set_order(order2);
//
//        // Zuordnung
//        order1.setOrderData(Set.of(data1));
//        order1.setOrderAllergens(Set.of(allergen1));
//
//        order2.setOrderData(Set.of(data2));
//        order2.setOrderAllergens(Set.of(allergen2));
//
//        List<Order> orders = List.of(order1, order2);
//
//        // **Hier wurde `Set<>` zu `List<>` geändert**
//        return new TourDTO(tour, driver, coDriver, List.of(institution1, institution2), List.of(address1, address2), orders);
//    }
//}