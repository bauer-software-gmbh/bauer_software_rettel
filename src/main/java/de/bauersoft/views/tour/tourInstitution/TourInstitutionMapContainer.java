package de.bauersoft.views.tour.tourInstitution;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.components.container.MapContainer;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.tour.tour.TourInstitution;
import de.bauersoft.data.entities.tour.tour.TourInstitutionKey;

public class TourInstitutionMapContainer extends MapContainer<TourInstitution, TourInstitutionKey, Institution>
{
    @Override
    public Container<TourInstitution, TourInstitutionKey> createContainer()
    {
        throw new UnsupportedOperationException("No Args Constructor is not supported");
    }

    @Override
    public TourInstitutionContainer createContainer(TourInstitution entity)
    {
        return new TourInstitutionContainer(entity);
    }

    @Override
    public TourInstitutionContainer createContainer(TourInstitution entity, ContainerState state)
    {
        return new TourInstitutionContainer(entity, state);
    }
}
