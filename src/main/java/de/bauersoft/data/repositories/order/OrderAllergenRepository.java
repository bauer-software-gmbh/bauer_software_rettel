package de.bauersoft.data.repositories.order;

import de.bauersoft.data.entities.order.OrderAllergen;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface OrderAllergenRepository extends JpaRepository<OrderAllergen, Long>, JpaSpecificationExecutor<OrderAllergen>
{
    @Transactional
    @Modifying
    @Query("""
            DELETE FROM OrderAllergen oa
            WHERE oa._order.id = :orderId
    """)
    void deleteAllByOrderId(Long orderId);
}
