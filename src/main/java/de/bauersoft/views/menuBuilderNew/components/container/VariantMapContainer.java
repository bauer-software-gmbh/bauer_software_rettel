package de.bauersoft.views.menuBuilderNew.components.container;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.components.container.MapContainer;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.entities.variant.Variant;

public class VariantMapContainer extends MapContainer<Variant, Long, Pattern>
{
    @Override
    public Container<Variant, Long> createContainer()
    {
        throw new UnsupportedOperationException("NoArgs constructor is not supported.");
    }

    @Override
    public VariantContainer createContainer(Variant entity)
    {
        return new VariantContainer(entity);
    }

    @Override
    public VariantContainer createContainer(Variant entity, ContainerState state)
    {
        return new VariantContainer(entity, state);
    }
}
