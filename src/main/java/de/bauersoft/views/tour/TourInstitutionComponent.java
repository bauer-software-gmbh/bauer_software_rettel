package de.bauersoft.views.tour;

import com.vaadin.flow.component.grid.Grid;
import de.bauersoft.data.entities.tourPlanning.tour.TourInstitution;
import de.bauersoft.services.InstitutionService;
import de.bauersoft.services.tourPlanning.TourInstitutionService;

public class TourInstitutionComponent
{
    private final InstitutionService institutionService;
    private final TourInstitutionService tourInstitutionService;

    private final Grid<TourInstitution> tourInstitutionGrid;


    public TourInstitutionComponent(InstitutionService institutionService, TourInstitutionService tourInstitutionService)
    {
        this.institutionService = institutionService;
        this.tourInstitutionService = tourInstitutionService;

        tourInstitutionGrid = new Grid<>(TourInstitution.class);
    }
}
