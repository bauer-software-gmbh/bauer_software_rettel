package de.bauersoft.views.institution.institutionFields.components.multiplier;

import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.institution.InstitutionMultiplier;
import de.bauersoft.data.entities.institution.InstitutionMultiplierKey;
import de.bauersoft.data.entities.institution.InstitutionPattern;
import de.bauersoft.data.entities.institution.InstitutionPatternKey;
import de.bauersoft.views.institution.container2.Container;

import java.util.Objects;

public class MultiplierContainer extends Container<InstitutionMultiplier, InstitutionMultiplierKey>
{
    private double tempMultiplier;

    public MultiplierContainer(InstitutionMultiplier entity)
    {
        super(entity);
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

    @Override
    public void loadTemporaries()
    {
        tempMultiplier = Objects.requireNonNullElse(getEntity().getMultiplier(), 0d);
    }

    @Override
    public void acceptTemporaries()
    {
        getEntity().setMultiplier(tempMultiplier);
    }
}
