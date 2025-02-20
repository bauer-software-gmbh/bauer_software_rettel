package de.bauersoft.views.institution.institutionFields.components.allergen;

import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.institution.InstitutionAllergen;
import de.bauersoft.data.entities.institution.InstitutionAllergenKey;
import de.bauersoft.views.institution.container2.Container;
import de.bauersoft.views.institution.container2.ContainerMapper;
import de.bauersoft.views.institution.container2.MapContainer;

public class AllergenMapContainer extends MapContainer<InstitutionAllergen, InstitutionAllergenKey, Allergen>
{
    @Override
    public Container<InstitutionAllergen, InstitutionAllergenKey> createContainer()
    {
        throw new IllegalArgumentException("No Args Constructor is disabled for this case.");
    }

    @Override
    public Container<InstitutionAllergen, InstitutionAllergenKey> createContainer(InstitutionAllergen entity)
    {
        return new AllergenContainer(entity);
    }
}
