package de.bauersoft.views.field.multiplier;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.fieldMultiplier.FieldMultiplier;
import de.bauersoft.data.entities.fieldMultiplier.FieldMultiplierKey;

public class MultiplierContainer extends Container<FieldMultiplier, FieldMultiplierKey>
{
    private double tempMultiplier;

    public MultiplierContainer(FieldMultiplier entity)
    {
        super(entity);
        loadTemporaries();
    }

    public MultiplierContainer(FieldMultiplier entity, ContainerState state)
    {
        super(entity, state);
        loadTemporaries();
    }

    public double getTempMultiplier()
    {
        return tempMultiplier;
    }

    public void setTempMultiplier(double tempMultiplier)
    {
        this.tempMultiplier = tempMultiplier;
    }

    @Override
    public MultiplierContainer loadTemporaries()
    {
        tempMultiplier = getEntity().getMultiplier();
        return this;
    }

    @Override
    public MultiplierContainer acceptTemporaries()
    {
        getEntity().setMultiplier(tempMultiplier);
        return this;
    }
}
