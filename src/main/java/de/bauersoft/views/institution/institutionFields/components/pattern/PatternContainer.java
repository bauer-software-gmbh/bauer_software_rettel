package de.bauersoft.views.institution.institutionFields.components.pattern;

import com.vaadin.flow.component.notification.Notification;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.institutionFieldPattern.InstitutionPattern;
import de.bauersoft.data.entities.institutionFieldPattern.InstitutionPatternKey;
import de.bauersoft.components.container.Container;

import java.util.Objects;

public class PatternContainer extends Container<InstitutionPattern, InstitutionPatternKey>
{
    private int tempAmount;
    private ContainerState tempState;

    public PatternContainer(InstitutionPattern entity)
    {
        super(entity);
        loadTemporaries();
    }

    public PatternContainer(InstitutionPattern entity, ContainerState state)
    {
        super(entity, state);
        loadTemporaries();
    }

    public int getTempAmount()
    {
        return tempAmount;
    }

    public void setTempAmount(int tempAmount)
    {
        this.tempAmount = tempAmount;
    }

    public ContainerState getTempState()
    {
        return tempState;
    }

    public void setTempState(ContainerState tempState)
    {
        this.tempState = tempState;
    }

    @Override
    public PatternContainer loadTemporaries()
    {
        tempAmount = Objects.requireNonNullElse(getEntity().getAmount(), 0);
        tempState = getState();
        return this;
    }

    @Override
    public PatternContainer acceptTemporaries()
    {
        getEntity().setAmount(tempAmount);
        setState(tempState);
        return this;
    }
}
