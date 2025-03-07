package de.bauersoft.views.institution.institutionFields.components.allergen;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergen;

import java.util.Set;


public class AllergenContainer extends Container<InstitutionAllergen, Long>
{
    private Set<Allergen> tempAllergens;
    private ContainerState tempState;

    private boolean isNew;

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

    public boolean isNew()
    {
        return isNew;
    }

    public AllergenContainer setIsNew(boolean isNew)
    {
        this.isNew = isNew;
        return this;
    }

    @Override
    public AllergenContainer loadTemporaries()
    {
        tempState = getState();
        return this;
    }

    @Override
    public AllergenContainer acceptTemporaries()
    {
        setState(tempState);
        return this;
    }
}
