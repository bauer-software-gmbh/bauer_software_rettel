package de.bauersoft.views.recipe.formulation;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.components.container.MapContainer;
import de.bauersoft.data.entities.formulation.Formulation;
import de.bauersoft.data.entities.formulation.FormulationKey;
import de.bauersoft.data.entities.ingredient.Ingredient;

public class FormulationMapContainer extends MapContainer<Formulation, FormulationKey, Ingredient>
{
    @Override
    public Container<Formulation, FormulationKey> createContainer()
    {
        throw new UnsupportedOperationException("No Args Constructor is not supported");
    }

    @Override
    public FormulationContainer createContainer(Formulation entity)
    {
        return new FormulationContainer(entity);
    }

    @Override
    public FormulationContainer createContainer(Formulation entity, ContainerState state)
    {
        return new FormulationContainer(entity, state);
    }
}
