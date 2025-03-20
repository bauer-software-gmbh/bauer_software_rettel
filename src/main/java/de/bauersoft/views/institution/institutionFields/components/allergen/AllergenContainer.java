package de.bauersoft.views.institution.institutionFields.components.allergen;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergen;

import java.util.HashSet;
import java.util.Set;

public class AllergenContainer extends Container<InstitutionAllergen, Long>
{
    private Set<Allergen> tempAllergens;

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

    public Set<Allergen> getTempAllergens()
    {
        return tempAllergens;
    }

    public AllergenContainer setTempAllergens(Set<Allergen> tempAllergens)
    {
        this.tempAllergens = tempAllergens;
        return this;
    }

    @Override
    public AllergenContainer loadTemporaries()
    {
        tempAllergens = new HashSet<>(getEntity().getAllergens());
        setTempState(getState());
        return this;
    }

    @Override
    public AllergenContainer acceptTemporaries()
    {
        getEntity().setAllergens(tempAllergens);
        setState(getTempState());
        return this;
    }
}
