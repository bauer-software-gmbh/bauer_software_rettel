package de.bauersoft.views.institution.container;

import de.bauersoft.services.ServiceBase;

import java.util.Collection;

public interface MappedStackContainerBase<TYPE extends ContainerID<ID>, ID, MAPPER>
{
    MappedContainerBase<TYPE, ID, MAPPER> ofEntity(TYPE entity);

    MappedStackContainerBase<TYPE, ID, MAPPER> add(MappedContainerBase<TYPE, ID, MAPPER> container);

    MappedStackContainerBase<TYPE, ID, MAPPER> remove(MappedContainerBase<TYPE, ID, MAPPER> container);

    Collection<? extends MappedContainerBase<TYPE, ID, MAPPER>> getContainers();

    MappedContainerBase<TYPE, ID, MAPPER> getContainer(MAPPER mapper);

    void clear();

    void run(ServiceBase<TYPE, ID> service);
}
