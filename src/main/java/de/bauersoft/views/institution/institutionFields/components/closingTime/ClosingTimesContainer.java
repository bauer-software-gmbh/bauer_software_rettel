package de.bauersoft.views.institution.institutionFields.components.closingTime;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.institutionClosingTime.InstitutionClosingTime;

import java.time.LocalDate;

public class ClosingTimesContainer extends Container<InstitutionClosingTime, Long>
{
    private int key;

    private ContainerState tempState;

    private LocalDate tempStartDate;
    private LocalDate tempEndDate;

    private String tempHeader;

    public ClosingTimesContainer(InstitutionClosingTime entity)
    {
        super(entity);
        loadTemporaries();
    }

    public ClosingTimesContainer(InstitutionClosingTime entity, ContainerState state)
    {
        super(entity, state);
        loadTemporaries();
    }

    public int getKey()
    {
        return key;
    }

    public ClosingTimesContainer setKey(int key)
    {
        this.key = key;
        return this;
    }

    public ContainerState getTempState()
    {
        return tempState;
    }

    public ClosingTimesContainer setTempState(ContainerState tempState)
    {
        this.tempState = tempState;
        return this;
    }

    public LocalDate getTempStartDate()
    {
        return tempStartDate;
    }

    public ClosingTimesContainer setTempStartDate(LocalDate tempStartDate)
    {
        this.tempStartDate = tempStartDate;
        return this;
    }

    public LocalDate getTempEndDate()
    {
        return tempEndDate;
    }

    public ClosingTimesContainer setTempEndDate(LocalDate tempEndDate)
    {
        this.tempEndDate = tempEndDate;
        return this;
    }

    public String getTempHeader()
    {
        return tempHeader;
    }

    public void setTempHeader(String tempHeader)
    {
        this.tempHeader = tempHeader;
    }

    @Override
    public ClosingTimesContainer loadTemporaries()
    {
        tempState = getState();

        tempStartDate = getEntity().getStartDate();
        tempEndDate = getEntity().getEndDate();

        tempHeader = getEntity().getHeader();
        return this;
    }

    @Override
    public ClosingTimesContainer acceptTemporaries()
    {
        setState(tempState);

        getEntity().setStartDate(tempStartDate);
        getEntity().setEndDate(tempEndDate);

        getEntity().setHeader(tempHeader);
        return this;
    }
}
