package de.bauersoft.components.container;

import com.vaadin.flow.component.notification.Notification;
import de.bauersoft.services.ServiceBase;

import java.util.Objects;

public abstract class Container<T extends ContainerID<ID>, ID>
{
    private T entity;

    private ContainerState state;

    public Container(T entity)
    {
        Objects.requireNonNull(entity);

        this.entity = entity;
        this.state = ContainerState.IGNORE;
    }

    public Container(T entity, ContainerState state)
    {
        Objects.requireNonNull(entity);

        this.entity = entity;
        this.state = state;
    }

    public T getEntity()
    {
        return entity;
    }

    public Container<T, ID> setEntity(T entity)
    {
        Objects.requireNonNull(entity);

        this.entity = entity;
        return this;
    }

    public ContainerState getState()
    {
        return state;
    }

    public Container<T, ID> setState(ContainerState state)
    {
        Objects.requireNonNull(state);
        this.state = state;

        return this;
    }

    public Container<T, ID> update(ServiceBase<T, ID> service)
    {
        service.update(entity);
        return this;
    }

    public Container<T, ID> delete(ServiceBase<T, ID> service)
    {
        if(!validateId()) return this;
        service.deleteById(entity.getId());
        return this;
    }

    public Container<T, ID> setID(ID id)
    {
        entity.setId(id);
        return this;
    }

    public ID getId()
    {
        return entity.getId();
    }

    public boolean validateId()
    {
        return entity.getId() != null;
    }

    public abstract Container<T, ID> loadTemporaries();
    public abstract Container<T, ID> acceptTemporaries();
}
