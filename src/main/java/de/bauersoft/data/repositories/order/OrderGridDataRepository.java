package de.bauersoft.data.repositories.order;

import de.bauersoft.data.entities.order.Order;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderGridDataRepository extends AbstractGridDataRepository<Order>
{
    public OrderGridDataRepository()
    {
        super(Order.class);
    }
}
