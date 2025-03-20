package de.bauersoft.views.vehicle.downtimes;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.tourPlanning.vehicle.VehicleDowntime;

import java.time.LocalDate;

public class DowntimeContainer extends Container<VehicleDowntime, Long>
{
    private LocalDate tempStartDate;
    private LocalDate tempEndDate;

    public DowntimeContainer(VehicleDowntime entity)
    {
        super(entity);
        loadTemporaries();
    }

    public DowntimeContainer(VehicleDowntime entity, ContainerState state)
    {
        super(entity, state);
        loadTemporaries();
    }

    public LocalDate getTempStartDate()
    {
        return tempStartDate;
    }

    public DowntimeContainer setTempStartDate(LocalDate tempStartDate)
    {
        this.tempStartDate = tempStartDate;
        return this;
    }

    public LocalDate getTempEndDate()
    {
        return tempEndDate;
    }

    public DowntimeContainer setTempEndDate(LocalDate tempEndDate)
    {
        this.tempEndDate = tempEndDate;
        return this;
    }

    @Override
    public DowntimeContainer loadTemporaries()
    {
        setTempState(getState());
        tempStartDate = getEntity().getStartDate();
        tempEndDate = getEntity().getEndDate();
        return this;
    }

    @Override
    public DowntimeContainer acceptTemporaries()
    {
        setState(getTempState());
        getEntity().setStartDate(tempStartDate);
        getEntity().setEndDate(tempEndDate);
        return this;
    }
}
