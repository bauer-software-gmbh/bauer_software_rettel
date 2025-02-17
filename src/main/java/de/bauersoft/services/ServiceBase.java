package de.bauersoft.services;


import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ServiceBase<T, ID>
{
    Optional<T> get(ID id);

    T update(T entity);

    List<T> updateAll(Collection<T> entities);

    void delete(T entity);

    void deleteById(ID id);

    void deleteAll(Collection<T> entities);

    void deleteAll();

    void deleteAllById(Collection<ID> ids);

    List<T> findAll();

    Page<T> list(Pageable pageable);

    Page<T> list(Pageable pageable, Specification<T> filter);

    long count();

    long count(List<SerializableFilter<T, ?>> filters);

    List<T> fetchAll(List<SerializableFilter<T, ?>> filters, List<QuerySortOrder> sortOrder);

    <E extends JpaRepository<T, ID> & JpaSpecificationExecutor<T>> E getRepository();

    GridDataRepository<T> getCustomRepository();
}
