package de.bauersoft.views.institution.institutionFields.components.pattern;

import de.bauersoft.data.entities.institution.InstitutionPattern;
import de.bauersoft.data.entities.institution.InstitutionPatternKey;
import de.bauersoft.views.institution.container2.Container;

import java.util.Objects;

public class PatternContainer extends Container<InstitutionPattern, InstitutionPatternKey>
{
    private int tempAmount;

    public PatternContainer(InstitutionPattern entity)
    {
        super(entity);
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
    public void loadTemporaries()
    {
        tempAmount = Objects.requireNonNullElse(getEntity().getAmount(), 0);
    }

    @Override
    public void acceptTemporaries()
    {
        getEntity().setAmount(tempAmount);
    }
}
