package de.bauersoft.views.recipe.formulation;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.formulation.Formulation;
import de.bauersoft.data.entities.formulation.FormulationKey;

public class FormulationContainer extends Container<Formulation, FormulationKey>
{
    private boolean isGridItem;

    private double tempQuantity;

    public FormulationContainer(Formulation entity)
    {
        super(entity);
        loadTemporaries();
    }

    public FormulationContainer(Formulation entity, ContainerState state)
    {
        super(entity, state);
        loadTemporaries();
    }

    public boolean isGridItem()
    {
        return isGridItem;
    }

    public void setGridItem(boolean gridItem)
    {
        isGridItem = gridItem;
    }

    public double getTempQuantity()
    {
        return tempQuantity;
    }

    public void setTempQuantity(double tempQuantity)
    {
        this.tempQuantity = tempQuantity;
    }

    @Override
    public FormulationContainer loadTemporaries()
    {
        setTempState(getState());
        tempQuantity = getEntity().getQuantity();
        return this;
    }

    @Override
    public FormulationContainer acceptTemporaries()
    {
        setState(getTempState());
        getEntity().setQuantity(tempQuantity);
        return this;
    }
}
