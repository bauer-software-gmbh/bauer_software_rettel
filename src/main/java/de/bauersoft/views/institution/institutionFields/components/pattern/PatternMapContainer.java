package de.bauersoft.views.institution.institutionFields.components.pattern;

import de.bauersoft.data.entities.institution.InstitutionPattern;
import de.bauersoft.data.entities.institution.InstitutionPatternKey;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.views.institution.container2.Container;
import de.bauersoft.views.institution.container2.MapContainer;

public class PatternMapContainer extends MapContainer<InstitutionPattern, InstitutionPatternKey, Pattern>
{
    @Override
    public Container<InstitutionPattern, InstitutionPatternKey> createContainer()
    {
        throw new IllegalArgumentException("No Args Constructor is disabled for this case.");
    }

    @Override
    public Container<InstitutionPattern, InstitutionPatternKey> createContainer(InstitutionPattern entity)
    {
        return new PatternContainer(entity);
    }
}
