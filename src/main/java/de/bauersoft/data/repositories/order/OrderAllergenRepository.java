package de.bauersoft.data.repositories.order;

import de.bauersoft.data.entities.order.OrderAllergen;
import de.bauersoft.data.entities.order.OrderAllergenKey;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderAllergenRepository extends JpaRepository<OrderAllergen, OrderAllergenKey>, JpaSpecificationExecutor<OrderAllergen>
{
    boolean existsByAllergenId(Long id);

    @Transactional
    @Modifying
    @Query("""
            DELETE FROM OrderAllergen oa
            WHERE oa.id.orderId = :orderId
    """)
    void deleteAllByOrderId(Long orderId);
}
