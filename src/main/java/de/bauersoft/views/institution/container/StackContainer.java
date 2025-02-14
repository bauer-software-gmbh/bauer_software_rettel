package de.bauersoft.views.institution.container;

import de.bauersoft.services.ServiceBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackContainer<T extends ContainerBase<ID, M>, ID, C extends Container<T, ID, M>, M>
{
    private final Map<M, C> mappedContainer;

    private final List<C> toUpdate;
    private final List<C> toDelete;

    public StackContainer()
    {
        mappedContainer = new HashMap<>();
        toUpdate = new ArrayList<>();
        toDelete = new ArrayList<>();
    }

    public C ofEntity(T entity)
    {
        return (C) new Container<>(entity);
    }

    public StackContainer removeContainer(C container)
    {
        mappedContainer.remove(container.getEntity().getMapper());
        toUpdate.remove(container);
        toDelete.remove(container);

        return this;
    }

    public StackContainer markAsUpdate(C container)
    {
        mappedContainer.putIfAbsent(container.getEntity().getMapper(), container);

        toUpdate.add(container);

        if(toDelete.contains(container))
            toDelete.remove(container);

        return this;
    }

    public StackContainer markAsDelete(C container)
    {
        mappedContainer.putIfAbsent(container.getEntity().getMapper(), container);

        toDelete.add(container);

        if(!toUpdate.contains(container))
            toUpdate.remove(container);

        return this;
    }

    public boolean isMarketToUpdate(C container)
    {
        return toUpdate.contains(container);
    }

    public boolean isMarketToRemove(C container)
    {
        return toDelete.contains(container);
    }

    public C getContainer(M mapper)
    {
        return mappedContainer.get(mapper);
    }

    public boolean containsContainer(M mapper)
    {
        return mappedContainer.containsKey(mapper);
    }

    public StackContainer clear()
    {
        mappedContainer.clear();
        toUpdate.clear();
        toDelete.clear();

        return this;
    }

    public StackContainer run(ServiceBase<T, ID> service)
    {
        toUpdate.forEach(container -> container.update(service));
        toDelete.forEach(container -> container.delete(service));

        return this;
    }

}
