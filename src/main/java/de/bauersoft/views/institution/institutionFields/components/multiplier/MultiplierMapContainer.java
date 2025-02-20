package de.bauersoft.views.institution.institutionFields.components.multiplier;

import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.institution.InstitutionMultiplier;
import de.bauersoft.data.entities.institution.InstitutionMultiplierKey;
import de.bauersoft.views.institution.container2.Container;
import de.bauersoft.views.institution.container2.MapContainer;

public class MultiplierMapContainer extends MapContainer<InstitutionMultiplier, InstitutionMultiplierKey, Course>
{
    @Override
    public Container<InstitutionMultiplier, InstitutionMultiplierKey> createContainer()
    {
        throw new IllegalArgumentException("No Args Constructor is disabled for this case.");
    }

    @Override
    public Container<InstitutionMultiplier, InstitutionMultiplierKey> createContainer(InstitutionMultiplier entity)
    {
        return new MultiplierContainer(entity);
    }
}
