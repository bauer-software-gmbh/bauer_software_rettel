package de.bauersoft.data.repositories.order;

import de.bauersoft.data.entities.order.OrderData;
import de.bauersoft.data.entities.order.OrderDataKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderDataRepository extends JpaRepository<OrderData, OrderDataKey>, JpaSpecificationExecutor<OrderData>
{
    boolean existsByVariantId(Long id);
}
