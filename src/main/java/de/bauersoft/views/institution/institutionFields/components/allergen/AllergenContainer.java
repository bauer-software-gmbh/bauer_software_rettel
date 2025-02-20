package de.bauersoft.views.institution.institutionFields.components.allergen;

import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.institution.InstitutionAllergen;
import de.bauersoft.data.entities.institution.InstitutionAllergenKey;
import de.bauersoft.views.institution.container2.Container;
import de.bauersoft.views.institution.container2.ContainerState;

import java.util.Objects;


public class AllergenContainer extends Container<InstitutionAllergen, InstitutionAllergenKey>
{
    private Allergen tempAllergen;
    private int tempAmount;
    private ContainerState tempState;

    public AllergenContainer(InstitutionAllergen entity)
    {
        super(entity);
        loadTemporaries();
    }

    public Allergen getTempAllergen()
    {
        return tempAllergen;
    }

    public void setTempAllergen(Allergen tempAllergen)
    {
        this.tempAllergen = tempAllergen;
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
    public void loadTemporaries()
    {
        tempAllergen = getEntity().getAllergen();
        tempAmount = Objects.requireNonNullElse(getEntity().getAmount(), 0);
    }

    @Override
    public void acceptTemporaries()
    {
        if(tempAllergen != null)
            getEntity().getId().setAllergenId(tempAllergen.getId());

        getEntity().setAllergen(tempAllergen);
        getEntity().setAmount(tempAmount);

        setState(tempState);
    }
}
