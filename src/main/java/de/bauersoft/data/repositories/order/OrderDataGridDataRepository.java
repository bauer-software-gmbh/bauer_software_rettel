package de.bauersoft.data.repositories.order;

import de.bauersoft.data.entities.order.OrderData;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderDataGridDataRepository extends AbstractGridDataRepository<OrderData>
{
    public OrderDataGridDataRepository()
    {
        super(OrderData.class);
    }
}
