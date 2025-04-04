package de.bauersoft.mobile.model.DTO;

import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.order.Order;

import de.bauersoft.data.entities.tour.tour.TourInstitution;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
public class InstitutionDTO {
    private Long id;
    private String name;
    private AddressDTO address;
    private Long essenAnz;
    private List<OrderDataDTO> meals;
    private AllergenDTO allergene;
    private String information;

    // 🔥 NEU: TourInstitution inkl. Ankunftszeit
    private LocalTime expectedArrivalTime;

    public InstitutionDTO(Institution institution, List<Order> orders, TourInstitution tourInstitution) {
        if (institution == null) {
            System.out.println("🚨 Institution ist NULL!");
            this.id = -1L;
            this.name = "Unbekannte Institution";
            this.address = null;
            this.essenAnz = 0L;
            this.allergene = null;
            this.information = "";
            return;
        }

        this.id = institution.getId() != null ? institution.getId() : -1L;
        this.name = institution.getName() != null ? institution.getName() : "Unbenannt";
        this.address = new AddressDTO(institution.getAddress());
        this.information = institution.getInformation() != null ? institution.getInformation() : "Unbekannte Institution";

        // Bestellungen & Allergene wie bisher
        List<Order> instiOrders = orders.stream()
                .filter(o -> o.getInstitution().getId().equals(institution.getId()))
                .toList();

        this.essenAnz = instiOrders.stream()
                .flatMap(order -> order.getOrderData().stream())
                .mapToLong(orderData -> Optional.ofNullable(orderData.getAmount()).orElse(0))
                .sum();

        Map<String, Long> allergenMap = instiOrders.stream()
                .flatMap(order -> order.getOrderAllergens().stream())
                .flatMap(orderAllergen -> orderAllergen.getAllergens().stream())
                .collect(Collectors.groupingBy(Allergen::getName, Collectors.counting()));

        this.allergene = new AllergenDTO(allergenMap);

        this.meals = instiOrders.stream()
                .flatMap(order -> order.getOrderData().stream())
                .map(OrderDataDTO::new)
                .collect(Collectors.toList());

        // 🕒 Ankunftszeit aus TourInstitution übernehmen
        this.expectedArrivalTime = (tourInstitution != null) ? tourInstitution.getExpectedArrivalTime() : null;
    }
}