package de.bauersoft.tools.listener.order;

import de.bauersoft.data.entities.order.OrderAllergen;

public record OrderAllergenChangeEvent(OrderAllergen orderAllergen, int oldAmount, int newAmount)
{
}
