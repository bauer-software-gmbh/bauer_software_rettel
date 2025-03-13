package de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer.orderLayer.variantLayer;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.components.container.MapContainer;
import de.bauersoft.data.entities.order.OrderData;
import de.bauersoft.data.entities.order.OrderDataKey;
import de.bauersoft.data.entities.variant.Variant;

public class VariantMapContainer extends MapContainer<OrderData, OrderDataKey, Variant>
{
    @Override
    public Container<OrderData, OrderDataKey> createContainer()
    {
        throw new UnsupportedOperationException("No Args Constructor is not supported");
    }

    @Override
    public VariantContainer createContainer(OrderData entity)
    {
        return new VariantContainer(entity);
    }

    @Override
    public VariantContainer createContainer(OrderData entity, ContainerState state)
    {
        return new VariantContainer(entity, state);
    }
}
