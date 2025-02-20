package de.bauersoft.views.institution.container2;

import de.bauersoft.services.ServiceBase;
import de.bauersoft.views.institution.container.ContainerID;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class MapContainer<T extends ContainerID<ID>, ID, M>
{
    private final Map<M, Container<T, ID>> containers;

    public MapContainer()
    {
        containers = new ConcurrentHashMap<>();
    }

    public abstract Container<T, ID> createContainer();

    public abstract Container<T, ID> createContainer(T entity);

    public Container<T, ID> addIfAbsent(M mapper, Supplier<T> entitySupplier)
    {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(entitySupplier);
        return containers.computeIfAbsent(mapper, k ->
        {
            T entity = entitySupplier.get();
            Objects.requireNonNull(entity);

            return createContainer(entity);
        });
    }

    public Container<T, ID> addIfAbsent(Supplier<Container<T, ID>> containerSupplier, M mapper)
    {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(containerSupplier);
        return containers.computeIfAbsent(mapper, k ->
        {
            Container<T, ID> container = containerSupplier.get();
            Objects.requireNonNull(container);

            return container;
        });
    }

    public void addContainer(M mapper, T entity)
    {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(entity);
        containers.put(mapper, createContainer(entity));
    }

    public void addContainer(M mapper, Container<T, ID> container)
    {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(container);
        containers.put(mapper, container);
    }

    public void removeContainer(M mapper)
    {
        if(mapper == null) return;
        containers.remove(mapper);
    }

    public Container<T, ID> getContainer(M mapper)
    {
        return (mapper == null) ? null : containers.get(mapper);
    }

    public List<Container<T, ID>> getContainers()
    {
        return List.copyOf(containers.values());
    }

    public Map<M, Container<T, ID>> getContainerMap()
    {
        return Map.copyOf(containers);
    }

    public boolean holdsContainer(M mapper)
    {
        return mapper != null && containers.containsKey(mapper);
    }

    public void loadTemporaries()
    {
        for(Container<T, ID> container : containers.values())
            container.loadTemporaries();
    }

    public void acceptTemporaries()
    {
        for(Container<T, ID> container : containers.values())
            container.acceptTemporaries();
    }

    /**
     * In dieser Methode soll für jeden Container die passende ID gesetzt werden.
     */
    public void evaluate(Consumer<Container<T, ID>> processor)
    {
        Objects.requireNonNull(processor);
        for(Container<T, ID> container : containers.values())
            processor.accept(container);
    }

    /**
     * Führt verschiedene Operationen für jeden Container je nach ContainerState aus.
     */
    public void run(ServiceBase<T, ID> service)
    {
        Objects.requireNonNull(service);
        for(Map.Entry<M, Container<T, ID>> entry : containers.entrySet())
        {
            Container<T, ID> container = entry.getValue();
            ContainerState state = container.getState();
            switch(state)
            {
                case UPDATE ->
                {
                    System.out.println("UPDATE: " + container.getEntity().toString());
                    container.update(service);
                }

                case DELETE ->
                {
                    System.out.println("DELETE: " + container.getEntity().toString());
                    container.delete(service);
                }
            }
        }
    }
}
