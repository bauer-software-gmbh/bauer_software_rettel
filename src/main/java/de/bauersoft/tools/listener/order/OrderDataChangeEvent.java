package de.bauersoft.tools.listener.order;

import de.bauersoft.data.entities.order.OrderData;

public record OrderDataChangeEvent(OrderData orderData, int oldAmount, int newAmount)
{
}
