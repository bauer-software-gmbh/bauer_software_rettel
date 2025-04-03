package de.bauersoft.views.menuBuilderNew.components.container;

import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.component.Component;
import de.bauersoft.data.entities.variant.Variant;

import java.util.HashSet;
import java.util.Set;

public class VariantContainer extends Container<Variant, Long>
{
    private String tempDescription;

    private Set<Component> tempComponents;

    public VariantContainer(Variant entity)
    {
        super(entity);
        loadTemporaries();
    }

    public VariantContainer(Variant entity, ContainerState state)
    {
        super(entity, state);
        loadTemporaries();
    }

    public String getTempDescription()
    {
        return tempDescription;
    }

    public VariantContainer setTempDescription(String tempDescription)
    {
        this.tempDescription = tempDescription;
        return this;
    }

    public Set<Component> getTempComponents()
    {
        return tempComponents;
    }

    public VariantContainer setTempComponents(Set<Component> tempComponents)
    {
        this.tempComponents = tempComponents;
        return this;
    }

    @Override
    public VariantContainer loadTemporaries()
    {
        setTempState(getState());
        tempDescription = getEntity().getDescription();
        tempComponents = new HashSet<>(getEntity().getComponents());
        return this;
    }

    @Override
    public VariantContainer acceptTemporaries()
    {
        setState(getTempState());
        getEntity().setDescription(tempDescription);
        getEntity().setComponents(new HashSet<>(tempComponents));
        return this;
    }
}
