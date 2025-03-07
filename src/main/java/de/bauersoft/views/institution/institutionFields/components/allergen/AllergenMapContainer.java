package de.bauersoft.views.institution.institutionFields.components.allergen;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.components.container.MapContainer;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergen;

public class AllergenMapContainer extends MapContainer<InstitutionAllergen, Long, Allergen>
{
    @Override
    public Container<InstitutionAllergen, Long> createContainer()
    {
        throw new IllegalArgumentException("No Args Constructor is disabled for this case.");
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
