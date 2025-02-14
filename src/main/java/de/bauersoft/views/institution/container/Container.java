package de.bauersoft.views.institution.container;

import de.bauersoft.services.ServiceBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Container<T extends ContainerBase<ID, M>, ID, M>
{
    private T entity;

    public Container update(ServiceBase<T, ID> service)
    {
        if(entity.getId() == null) return this;
        service.update(entity);

        return this;
    }

    public Container delete(ServiceBase<T, ID> service)
    {
        if(entity.getId() == null) return this;
        service.delete(entity);

        return this;
    }
}
