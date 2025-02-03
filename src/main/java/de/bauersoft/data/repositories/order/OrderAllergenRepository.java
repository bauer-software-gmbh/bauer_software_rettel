package de.bauersoft.data.repositories.order;

import de.bauersoft.data.entities.order.OrderAllergen;
import de.bauersoft.data.entities.order.OrderAllergenKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OrderAllergenRepository extends JpaRepository<OrderAllergen, OrderAllergenKey>, JpaSpecificationExecutor<OrderAllergen>
{
    boolean existsByAllergenId(Long id);
}
