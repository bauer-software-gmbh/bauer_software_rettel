package de.bauersoft.views.closingTime;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.institutionClosingTime.InstitutionClosingTime;

import java.time.LocalDate;

public class ClosingTimeContainer extends Container<InstitutionClosingTime, Long>
{
    private LocalDate tempStartDate;
    private LocalDate tempEndDate;
    private String tempHeader;

    private ContainerState tempState;

    public ClosingTimeContainer(InstitutionClosingTime entity)
    {
        super(entity);
        loadTemporaries();
    }

    public ClosingTimeContainer(InstitutionClosingTime entity, ContainerState state)
    {
        super(entity, state);
        loadTemporaries();
    }

    @Override
    public ClosingTimeContainer loadTemporaries()
    {
        tempStartDate = getEntity().getStartDate();
        tempEndDate = getEntity().getEndDate();
        tempHeader = getEntity().getHeader();
        tempState = getState();
        return this;
    }

    @Override
    public ClosingTimeContainer acceptTemporaries()
    {
        getEntity().setStartDate(tempStartDate);
        getEntity().setEndDate(tempEndDate);
        getEntity().setHeader(tempHeader);
        setState(tempState);
        return this;
    }
}
