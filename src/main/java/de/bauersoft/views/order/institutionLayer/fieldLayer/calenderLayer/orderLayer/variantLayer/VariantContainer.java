package de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer.orderLayer.variantLayer;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.order.OrderData;
import de.bauersoft.data.entities.order.OrderDataKey;

public class VariantContainer extends Container<OrderData, OrderDataKey>
{
    private int tempAmount;

    public VariantContainer(OrderData entity)
    {
        super(entity);
        loadTemporaries();
    }

    public VariantContainer(OrderData entity, ContainerState state)
    {
        super(entity, state);
        loadTemporaries();
    }

    public int getTempAmount()
    {
        return tempAmount;
    }

    public VariantContainer setTempAmount(int tempAmount)
    {
        this.tempAmount = tempAmount;
        return this;
    }

    @Override
    public VariantContainer loadTemporaries()
    {
        tempAmount = getEntity().getAmount();
        setTempState(getState());
        return this;
    }

    @Override
    public VariantContainer acceptTemporaries()
    {
        getEntity().setAmount(tempAmount);
        setState(getTempState());;
        return this;
    }
}
