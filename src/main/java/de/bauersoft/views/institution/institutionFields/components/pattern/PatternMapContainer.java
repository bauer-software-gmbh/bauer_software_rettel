package de.bauersoft.views.institution.institutionFields.components.pattern;

import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.institutionFieldPattern.InstitutionPattern;
import de.bauersoft.data.entities.institutionFieldPattern.InstitutionPatternKey;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.MapContainer;

public class PatternMapContainer extends MapContainer<InstitutionPattern, InstitutionPatternKey, Pattern>
{
    @Override
    public Container<InstitutionPattern, InstitutionPatternKey> createContainer()
    {
        throw new IllegalArgumentException("No Args Constructor is disabled for this case.");
    }

    @Override
    public PatternContainer createContainer(InstitutionPattern entity)
    {
        return new PatternContainer(entity);
    }

    @Override
    public PatternContainer createContainer(InstitutionPattern entity, ContainerState state)
    {
        return new PatternContainer(entity, state);
    }
}
