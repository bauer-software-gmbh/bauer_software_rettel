package de.bauersoft.views.institution.institutionFields.components.allergen;

import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergen;
import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergenKey;
import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;

import java.util.Objects;


public class AllergenContainer extends Container<InstitutionAllergen, InstitutionAllergenKey>
{
    private int tempAmount;
    private ContainerState tempState;

    public AllergenContainer(InstitutionAllergen entity)
    {
        super(entity);
        loadTemporaries();
    }

    public AllergenContainer(InstitutionAllergen entity, ContainerState state)
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

    @Override
    public AllergenContainer setState(ContainerState state)
    {
        super.setState(state);
        this.tempState = state;

        return this;
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
    public AllergenContainer loadTemporaries()
    {
        tempAmount = Objects.requireNonNullElse(getEntity().getAmount(), 0);
        tempState = getState();
        return this;
    }

    @Override
    public AllergenContainer acceptTemporaries()
    {
        getEntity().setAmount(tempAmount);

        setState(tempState);
        return this;
    }
}
