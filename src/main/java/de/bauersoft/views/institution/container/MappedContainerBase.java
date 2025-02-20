package de.bauersoft.views.institution.container;

import de.bauersoft.services.ServiceBase;

public interface MappedContainerBase<TYPE, ID, MAPPER>
{
    TYPE getEntity();

    void setEntity(TYPE entity);

    void markToUpdate();

    boolean isMarkedToUpdate();

    MappedContainerBase<TYPE, ID, MAPPER> update(ServiceBase<TYPE, ID> service);

    void markToDelete();

    boolean isMarkedToDelete();

    MappedContainerBase<TYPE, ID, MAPPER> delete(ServiceBase<TYPE, ID> service);

    MappedContainerBase<TYPE, ID, MAPPER> accept(ID id);

    boolean validateId();

    MAPPER getMapper();
}
