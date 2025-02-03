package de.bauersoft.data.repositories.order;

import de.bauersoft.data.entities.order.OrderAllergen;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderAllergenGridDataRepository extends AbstractGridDataRepository<OrderAllergen>
{
    public OrderAllergenGridDataRepository()
    {
        super(OrderAllergen.class);
    }
}
