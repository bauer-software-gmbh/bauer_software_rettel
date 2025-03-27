package de.bauersoft.views.vehicle.downtimes;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.components.container.MapContainer;
import de.bauersoft.data.entities.tour.vehicle.VehicleDowntime;

public class DowntimeMapContainer extends MapContainer<VehicleDowntime, Long, Integer>
{
    @Override
    public Container<VehicleDowntime, Long> createContainer()
    {
        throw new UnsupportedOperationException("No Args Constructor is not supported");
    }

    @Override
    public DowntimeContainer createContainer(VehicleDowntime entity)
    {
        return new DowntimeContainer(entity);
    }

    @Override
    public DowntimeContainer createContainer(VehicleDowntime entity, ContainerState state)
    {
        return new DowntimeContainer(entity, state);
    }
}
