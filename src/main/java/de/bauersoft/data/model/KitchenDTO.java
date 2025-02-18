package de.bauersoft.data.model;

import de.bauersoft.data.entities.component.Component;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.data.entities.institution.InstitutionMultiplier;
import de.bauersoft.data.entities.order.Order;
import de.bauersoft.data.entities.order.OrderData;
import de.bauersoft.data.entities.pattern.DefaultPattern;
import de.bauersoft.services.InstitutionFieldsService;
import de.bauersoft.services.OrderService;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class KitchenDTO {
    private final OrderService orderService;
    private final InstitutionFieldsService institutionFieldsService;

    private List<Order> localDateOrders;
    private final Map<Long, InstitutionField> institutionFieldsMap = new HashMap<>();
    private final Map<Long, Map<Course, InstitutionMultiplier>> multipliersMap = new HashMap<>();
    private final Map<Long, Map<Course, Component>> componentMap = new HashMap<>();

    public KitchenDTO(OrderService orderService, InstitutionFieldsService institutionFieldsService) {
        this.orderService = orderService;
        this.institutionFieldsService = institutionFieldsService;
        updateData(); // 🔥 Direkt beim Erstellen ausführen!
    }

    // 🔥 Gibt ALLE relevanten Daten für einen bestimmten Course zurück
    public List<OrderData> getFilteredData(Course course) {
        return localDateOrders.stream()
                .flatMap(order -> order.getOrderData().stream())
                .filter(orderData -> orderData.getVariant().getComponents()
                        .stream()
                        .anyMatch(component -> component.getCourse().equals(course)))
                .toList();
    }

    public Map<Course, InstitutionMultiplier> getMultipliers(long institutionId, long fieldId) {
        return multipliersMap.getOrDefault(institutionId * 10000 + fieldId, Collections.emptyMap());
    }

    public Map<Course, Component> getComponents(long institutionId, long fieldId) {
        return componentMap.getOrDefault(institutionId * 10000 + fieldId, Collections.emptyMap());
    }

    public Optional<InstitutionField> getInstitutionField(long institutionId, long fieldId) {
        return Optional.ofNullable(institutionFieldsMap.get(institutionId * 10000 + fieldId));
    }

    public Optional<Integer> getChildCount(long institutionId, long fieldId) {
        return Optional.ofNullable(institutionFieldsMap.get(institutionId * 10000 + fieldId))
                .map(InstitutionField::getChildCount);
    }

    public Optional<Integer> getOrderAmount(long institutionId, long fieldId)
    {
        Optional<Order> order = orderService.findAllByOrderDateAndInstitutionIdAndFieldId(LocalDate.now(), institutionId, fieldId);

        return order.flatMap(o -> o.getOrderData().stream()
                .filter(item -> DefaultPattern.DEFAULT.equalsDefault(item.getVariant().getPattern()))
                .map(OrderData::getAmount)
                .findFirst());
    }

    public void updateData() {
        LocalDate today = LocalDate.now();
        System.out.println("🔄 [KitchenDTO] Update gestartet für " + today);

        this.localDateOrders = orderService.getOrdersForLocalDate(today);
        System.out.println("🔍 Anzahl geladener Orders: " + localDateOrders.size());

        institutionFieldsMap.clear();
        multipliersMap.clear();
        componentMap.clear();

        for (InstitutionField institutionField : institutionFieldsService.findAll()) {
            long institutionId = institutionField.getInstitution().getId();
            long fieldId = institutionField.getField().getId();
            long key = institutionId * 10000 + fieldId;

            System.out.println("✅ InstitutionField gespeichert: " + institutionField.getInstitution().getName());

            institutionFieldsMap.put(key, institutionField);

            /*Set<InstitutionMultiplier> multipliers = institutionField.getInstitution().getInstitutionMultipliers();
            multipliersMap.put(key,
                    multipliers.stream().collect(Collectors.toMap(InstitutionMultiplier::getCourse, m -> m))
            );*/

            Optional<Order> order = orderService.findAllByOrderDateAndInstitutionIdAndFieldId(today, institutionId, fieldId);
            order.flatMap(o -> o.getOrderData().stream()
                    .filter(item -> DefaultPattern.DEFAULT.equalsDefault(item.getVariant().getPattern()))
                    .findFirst()).ifPresent(od -> {
                componentMap.put(key,
                        od.getVariant().getComponents().stream()
                                .collect(Collectors.toMap(Component::getCourse, c -> c))
                );
                System.out.println("✅ Komponenten für Institution " + institutionId + " gespeichert: " + od.getVariant().getComponents().size());
            });
        }
        System.out.println("🔄 [KitchenDTO] Update abgeschlossen.");
    }
}

