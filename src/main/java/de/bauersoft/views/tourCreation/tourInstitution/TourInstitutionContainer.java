package de.bauersoft.views.tourCreation.tourInstitution;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.tour.tour.TourInstitution;
import de.bauersoft.data.entities.tour.tour.TourInstitutionKey;

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

    @Override
    public ContainerState getState()
    {
        return super.getState();
    }

    @Override
    public Container<TourInstitution, TourInstitutionKey> setState(ContainerState state)
    {
        return super.setState(state);
    }
}
