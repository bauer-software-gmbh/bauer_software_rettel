package de.bauersoft.views.institution.institutionFields.components.multiplier;

import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.institutionFieldMultiplier.InstitutionMultiplier;
import de.bauersoft.data.entities.institutionFieldMultiplier.InstitutionMultiplierKey;
import de.bauersoft.components.container.Container;

import java.util.Objects;

public class MultiplierContainer extends Container<InstitutionMultiplier, InstitutionMultiplierKey>
{
    private double tempMultiplier;
    private ContainerState tempState;

    public MultiplierContainer(InstitutionMultiplier entity)
    {
        super(entity);
        loadTemporaries();
    }

    public MultiplierContainer(InstitutionMultiplier entity, ContainerState state)
    {
        super(entity, state);
        loadTemporaries();
    }

    public double getTempMultiplier()
    {
        return tempMultiplier;
    }

    public void setTempMultiplier(double tempMultiplier)
    {
        this.tempMultiplier = tempMultiplier;
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
    public MultiplierContainer loadTemporaries()
    {
        tempMultiplier = Objects.requireNonNullElse(getEntity().getMultiplier(), 0d);
        tempState = getState();
        return this;
    }

    @Override
    public MultiplierContainer acceptTemporaries()
    {
        getEntity().setMultiplier(tempMultiplier);
        setState(tempState);
        return this;
    }
}
