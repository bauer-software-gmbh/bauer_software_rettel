package de.bauersoft.views.vehicle.downtimes;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.tour.vehicle.VehicleDowntime;

import java.time.LocalDate;

public class DowntimeContainer extends Container<VehicleDowntime, Long>
{
    private String tempHeader;
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

    public String getTempHeader()
    {
        return tempHeader;
    }

    public void setTempHeader(String tempHeader)
    {
        this.tempHeader = tempHeader;
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
        tempHeader = getEntity().getHeader();
        tempStartDate = getEntity().getStartDate();
        tempEndDate = getEntity().getEndDate();
        return this;
    }

    @Override
    public DowntimeContainer acceptTemporaries()
    {
        setState(getTempState());
        getEntity().setHeader(tempHeader);
        getEntity().setStartDate(tempStartDate);
        getEntity().setEndDate(tempEndDate);
        return this;
    }
}
