package de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer.orderLayer.allergenLayer;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.components.container.MapContainer;
import de.bauersoft.data.entities.order.OrderAllergen;

public class AllergenMapContainer extends MapContainer<OrderAllergen, Long, Integer>
{
    @Override
    public Container<OrderAllergen, Long> createContainer()
    {
        throw new UnsupportedOperationException("No Args Constructor is not supported");
    }

    @Override
    public AllergenContainer createContainer(OrderAllergen entity)
    {
        return new AllergenContainer(entity);
    }

    @Override
    public AllergenContainer createContainer(OrderAllergen entity, ContainerState state)
    {
        return new AllergenContainer(entity, state);
    }
}
