package de.bauersoft.views.institution.institutionFields.components.multiplier;

import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.institutionFieldMultiplier.InstitutionMultiplier;
import de.bauersoft.data.entities.institutionFieldMultiplier.InstitutionMultiplierKey;
import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.MapContainer;

public class MultiplierMapContainer extends MapContainer<InstitutionMultiplier, InstitutionMultiplierKey, Course>
{
    @Override
    public Container<InstitutionMultiplier, InstitutionMultiplierKey> createContainer()
    {
        throw new IllegalArgumentException("No Args Constructor is disabled for this case.");
    }

    @Override
    public MultiplierContainer createContainer(InstitutionMultiplier entity)
    {
        return new MultiplierContainer(entity);
    }

    @Override
    public MultiplierContainer createContainer(InstitutionMultiplier entity, ContainerState state)
    {
        return new MultiplierContainer(entity, state);
    }
}
