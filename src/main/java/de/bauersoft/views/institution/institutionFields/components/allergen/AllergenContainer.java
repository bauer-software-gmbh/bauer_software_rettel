package de.bauersoft.views.institution.institutionFields.components.allergen;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergen;

import java.util.HashSet;
import java.util.Set;

public class AllergenContainer extends Container<InstitutionAllergen, Long>
{
    private int mapper;

    private ContainerState tempState;
    private Set<Allergen> tempAllergens;

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

    public int getMapper()
    {
        return mapper;
    }

    public AllergenContainer setMapper(int mapper)
    {
        this.mapper = mapper;
        return this;
    }

    public ContainerState getTempState()
    {
        return tempState;
    }

    public AllergenContainer setTempState(ContainerState tempState)
    {
        this.tempState = tempState;
        return this;
    }

    public Set<Allergen> getTempAllergens()
    {
        return tempAllergens;
    }

    public AllergenContainer setTempAllergens(Set<Allergen> tempAllergens)
    {
        this.tempAllergens = tempAllergens;
        return this;
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
        tempAllergens = new HashSet<>(getEntity().getAllergens());
        return this;
    }

    @Override
    public AllergenContainer acceptTemporaries()
    {
        setState(tempState);
        getEntity().setAllergens(tempAllergens);
        return this;
    }
}
