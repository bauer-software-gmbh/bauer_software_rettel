package de.bauersoft.views.institution.institutionFields.components.closingTime;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.components.container.MapContainer;
import de.bauersoft.data.entities.institutionClosingTime.InstitutionClosingTime;

public class ClosingTimesMapContainer extends MapContainer<InstitutionClosingTime, Long, Integer>
{
    private int key;

    public ClosingTimesMapContainer()
    {
        super();
        key = 0;
    }

    @Override
    public Container<InstitutionClosingTime, Long> createContainer()
    {
        throw new UnsupportedOperationException("No Args Constructor not supported");
    }

    @Override
    public ClosingTimesContainer createContainer(InstitutionClosingTime entity)
    {
        return new ClosingTimesContainer(entity);
    }

    @Override
    public Container<InstitutionClosingTime, Long> createContainer(InstitutionClosingTime entity, ContainerState state)
    {
        return new ClosingTimesContainer(entity, state);
    }

    public synchronized int getNextKey()
    {
        return key++;
    }
}
