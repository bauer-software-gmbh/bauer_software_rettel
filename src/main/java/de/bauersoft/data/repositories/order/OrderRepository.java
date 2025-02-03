package de.bauersoft.data.repositories.order;

import de.bauersoft.data.entities.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order>
{
    boolean existsByFieldId(Long id);
}
