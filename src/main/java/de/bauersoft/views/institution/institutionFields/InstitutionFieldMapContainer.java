package de.bauersoft.views.institution.institutionFields;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.components.container.MapContainer;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institutionField.InstitutionField;

public class InstitutionFieldMapContainer extends MapContainer<InstitutionField, Long, Field>
{
    @Override
    public Container<InstitutionField, Long> createContainer()
    {
        throw new UnsupportedOperationException("No Args Constructor is not supported");
    }

    @Override
    public InstitutionFieldContainer createContainer(InstitutionField entity)
    {
        return new InstitutionFieldContainer(entity);
    }

    @Override
    public InstitutionFieldContainer createContainer(InstitutionField entity, ContainerState state)
    {
        return new InstitutionFieldContainer(entity, state);
    }
}
