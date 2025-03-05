package de.bauersoft.views.closingTime;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.components.container.MapContainer;
import de.bauersoft.data.entities.institutionClosingTime.InstitutionClosingTime;

public class ClosingTimeMapContainer extends MapContainer<InstitutionClosingTime, Long, Integer>
{
    @Override
    public Container<InstitutionClosingTime, Long> createContainer()
    {
        throw new UnsupportedOperationException("No Args Constructor is not supported");
    }

    @Override
    public Container<InstitutionClosingTime, Long> createContainer(InstitutionClosingTime entity)
    {
        return new ClosingTimeContainer(entity);
    }

    @Override
    public Container<InstitutionClosingTime, Long> createContainer(InstitutionClosingTime entity, ContainerState state)
    {
        return new ClosingTimeContainer(entity, state);
    }
}
