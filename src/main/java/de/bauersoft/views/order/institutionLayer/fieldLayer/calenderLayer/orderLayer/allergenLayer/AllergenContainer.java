package de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer.orderLayer.allergenLayer;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.order.OrderAllergen;

import java.util.HashSet;
import java.util.Set;

public class AllergenContainer extends Container<OrderAllergen, Long>
{
    private Set<Allergen> tempAllergens;

    public AllergenContainer(OrderAllergen entity)
    {
        super(entity);
        loadTemporaries();
    }

    public AllergenContainer(OrderAllergen entity, ContainerState state)
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
        getEntity().setAllergens(new HashSet<>(tempAllergens));
        setState(getTempState());
        return this;
    }
}
