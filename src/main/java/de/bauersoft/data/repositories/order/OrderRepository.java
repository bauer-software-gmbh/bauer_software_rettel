package de.bauersoft.data.repositories.order;

import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order>
{
    boolean existsByFieldId(Long id);

    boolean existsByInstitution(Institution institution);

    Optional<Order> findByOrderDateAndInstitutionAndField(LocalDate orderDate, Institution institution, Field field);
}
