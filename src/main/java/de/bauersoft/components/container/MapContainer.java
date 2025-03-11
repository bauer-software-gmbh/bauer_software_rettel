package de.bauersoft.components.container;

import com.vaadin.flow.component.notification.Notification;
import de.bauersoft.services.ServiceBase;
import de.bauersoft.views.institution.institutionFields.components.allergen.AllergenContainer;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class MapContainer<T extends ContainerID<ID>, ID, M>
{
    private int mapper;

    private final Map<M, Container<T, ID>> containers;

    public MapContainer()
    {
        containers = new ConcurrentHashMap<>();

        mapper = 1;
    }

    public abstract Container<T, ID> createContainer();

    public abstract Container<T, ID> createContainer(T entity);

    public abstract Container<T, ID> createContainer(T entity, ContainerState state);

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

    public Container<T, ID> addIfAbsent(M mapper, Supplier<T> entitySupplier, Consumer<Container<T, ID>> onNew)
    {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(entitySupplier);
        return containers.computeIfAbsent(mapper, k ->
        {
            T entity = entitySupplier.get();
            Objects.requireNonNull(entity);

            Container<T, ID> container = createContainer(entity);
            onNew.accept(container);

            return container;
        });
    }

    public Container<T, ID> addIfAbsent(M mapper, Supplier<T> entitySupplier, ContainerState state)
    {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(entitySupplier);
        return containers.computeIfAbsent(mapper, k ->
        {
            T entity = entitySupplier.get();
            Objects.requireNonNull(entity);

            return createContainer(entity, state);
        });
    }

    public Container<T, ID> addIfAbsent(M mapper, Supplier<T> entitySupplier, Consumer<Container<T, ID>> onNew, ContainerState state)
    {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(entitySupplier);
        return containers.computeIfAbsent(mapper, k ->
        {
            T entity = entitySupplier.get();
            Objects.requireNonNull(entity);

            Container<T, ID> container = createContainer(entity, state);
            onNew.accept(container);

            return container;
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

    public Container<T, ID> addContainer(M mapper, T entity)
    {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(entity);

        Container<T, ID> container = createContainer(entity);
        containers.put(mapper, container);

        return container;
    }

    public Container<T, ID> addContainer(M mapper, T entity, ContainerState state)
    {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(entity);

        Container<T, ID> container = createContainer(entity, state);
        containers.put(mapper, container);

        return container;
    }

    public Container<T, ID> addContainer(M mapper, Container<T, ID> container)
    {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(container);

        containers.put(mapper, container);
        return container;
    }

    public Container<T, ID> removeContainer(M mapper)
    {
        if(mapper == null) return null;
        return containers.remove(mapper);
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

    public MapContainer<T, ID, M> clear()
    {
        containers.clear();
        return this;
    }

    public MapContainer<T, ID, M> loadTemporaries()
    {
        for(Container<T, ID> container : containers.values())
            container.loadTemporaries();

        return this;
    }

    public MapContainer<T, ID, M> acceptTemporaries()
    {
        for(Container<T, ID> container : containers.values())
            container.acceptTemporaries();

        return this;
    }

    /**
     * In dieser Methode soll für jeden Container die passende ID gesetzt werden.
     */
    public MapContainer<T, ID, M> evaluate(Consumer<Container<T, ID>> processor)
    {
        Objects.requireNonNull(processor);
        for(Container<T, ID> container : containers.values())
            processor.accept(container);

        return this;
    }

    /**
     * Führt verschiedene Operationen für jeden Container je nach ContainerState aus.
     */
    public MapContainer<T, ID, M> run(ServiceBase<T, ID> service)
    {
        Objects.requireNonNull(service);
        for(Map.Entry<M, Container<T, ID>> entry : containers.entrySet())
        {
            Container<T, ID> container = entry.getValue();
            ContainerState state = container.getState();

            if(container instanceof AllergenContainer allergenContainer)
            {
                Notification.show(allergenContainer.getTempState() + " - s " + allergenContainer.getState());
            }

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

        return this;
    }

    public int getMapper()
    {
        return mapper;
    }

    public synchronized int nextMapper()
    {
        return ++mapper;
    }
}
