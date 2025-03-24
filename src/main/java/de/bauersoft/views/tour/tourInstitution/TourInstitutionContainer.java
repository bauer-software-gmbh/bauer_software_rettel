package de.bauersoft.views.tour.tourInstitution;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.tourPlanning.tour.TourInstitution;
import de.bauersoft.data.entities.tourPlanning.tour.TourInstitutionKey;

import java.time.LocalTime;

public class TourInstitutionContainer extends Container<TourInstitution, TourInstitutionKey>
{
    private boolean isGridItem;

    private LocalTime tempExpectedArrivalTime;

    public TourInstitutionContainer(TourInstitution entity)
    {
        super(entity);
        loadTemporaries();
    }

    public TourInstitutionContainer(TourInstitution entity, ContainerState state)
    {
        super(entity, state);
        loadTemporaries();
    }

    public boolean isGridItem()
    {
        return isGridItem;
    }

    public void setGridItem(boolean gridItem)
    {
        isGridItem = gridItem;
    }

    public LocalTime getTempExpectedArrivalTime()
    {
        return tempExpectedArrivalTime;
    }

    public void setTempExpectedArrivalTime(LocalTime tempExpectedArrivalTime)
    {
        this.tempExpectedArrivalTime = tempExpectedArrivalTime;
    }

    @Override
    public TourInstitutionContainer loadTemporaries()
    {
        setTempState(getState());
        tempExpectedArrivalTime = getEntity().getExpectedArrivalTime();
        return this;
    }

    @Override
    public TourInstitutionContainer acceptTemporaries()
    {
        setState(getTempState());
        getEntity().setExpectedArrivalTime(tempExpectedArrivalTime);
        return this;
    }
}
