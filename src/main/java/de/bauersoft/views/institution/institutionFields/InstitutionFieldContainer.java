package de.bauersoft.views.institution.institutionFields;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.institutionField.InstitutionField;

public class InstitutionFieldContainer extends Container<InstitutionField, Long>
{
    private boolean isGridItem;

    public InstitutionFieldContainer(InstitutionField entity)
    {
        super(entity);
        loadTemporaries();
    }

    public InstitutionFieldContainer(InstitutionField entity, ContainerState state)
    {
        super(entity, state);
        loadTemporaries();
    }

    public boolean isGridItem()
    {
        return isGridItem;
    }

    public InstitutionFieldContainer setGridItem(boolean gridItem)
    {
        isGridItem = gridItem;
        return this;
    }

    @Override
    public InstitutionFieldContainer loadTemporaries()
    {
        setTempState(getState());
        return this;
    }

    @Override
    public InstitutionFieldContainer acceptTemporaries()
    {
        setState(getTempState());
        return this;
    }
}
