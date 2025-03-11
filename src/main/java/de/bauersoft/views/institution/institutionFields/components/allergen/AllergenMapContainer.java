package de.bauersoft.views.institution.institutionFields.components.allergen;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.components.container.MapContainer;
import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergen;

public class AllergenMapContainer extends MapContainer<InstitutionAllergen, Long, Integer>
{
    @Override
    public Container<InstitutionAllergen, Long> createContainer()
    {
        throw new UnsupportedOperationException("No Args Constructor not supported");
    }

    @Override
    public AllergenContainer createContainer(InstitutionAllergen entity)
    {
        return new AllergenContainer(entity);
    }

    @Override
    public AllergenContainer createContainer(InstitutionAllergen entity, ContainerState state)
    {
        return new AllergenContainer(entity, state);
    }
}
