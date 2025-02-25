package de.bauersoft.views.field.multiplier;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.components.container.MapContainer;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.fieldMultiplier.FieldMultiplier;
import de.bauersoft.data.entities.fieldMultiplier.FieldMultiplierKey;

public class MultiplierMapContainer extends MapContainer<FieldMultiplier, FieldMultiplierKey, Course>
{
    @Override
    public MultiplierContainer createContainer()
    {
        throw new UnsupportedOperationException("No Args Constructor is disabled for this case.");
    }

    @Override
    public MultiplierContainer createContainer(FieldMultiplier entity)
    {
        return new MultiplierContainer(entity);
    }

    @Override
    public Container<FieldMultiplier, FieldMultiplierKey> createContainer(FieldMultiplier entity, ContainerState state)
    {
        return new MultiplierContainer(entity, state);
    }
}
