package de.bauersoft.views.institution.container2;

import com.vaadin.flow.component.notification.Notification;
import de.bauersoft.services.ServiceBase;
import de.bauersoft.views.institution.container.ContainerID;

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

    public T getEntity()
    {
        return entity;
    }

    public void setEntity(T entity)
    {
        this.entity = entity;
    }

    public ContainerState getState()
    {
        return state;
    }

    public void setState(ContainerState state)
    {
        Objects.requireNonNull(state);
        this.state = state;
    }

    public void update(ServiceBase<T, ID> service)
    {
        if(!hasID())
            throw new IllegalStateException("The entity must have an ID.");

        service.update(entity);
    }

    public void delete(ServiceBase<T, ID> service)
    {
        if(!hasID())
            throw new IllegalStateException("The entity must have an ID.");

        service.deleteById(entity.getId());
    }

    public void setID(ID id)
    {
        entity.setId(id);
    }

    public ID getID()
    {
        return entity.getId();
    }

    public boolean hasID()
    {
        return entity.getId() != null;
    }

    public abstract void loadTemporaries();
    public abstract void acceptTemporaries();
}
