package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.entities.component.Component;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.component.ComponentGridDataRepository;
import de.bauersoft.data.repositories.component.ComponentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ComponentService implements ServiceBase<Component, Long>
{
    private final ComponentRepository repository;
    private final ComponentGridDataRepository customRepository;

    public ComponentService(ComponentRepository repository, ComponentGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<Component> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Component update(Component entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<Component> updateAll(Collection<Component> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(Component entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<Component> entities)
    {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll()
    {
        repository.deleteAll();
    }

    @Override
    public void deleteAllById(Collection<Long> ids)
    {
        repository.deleteAllById(ids);
    }

    @Override
    public List<Component> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<Component> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Component> list(Pageable pageable, Specification<Component> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Component, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<Component> fetchAll(List<SerializableFilter<Component, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public ComponentRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Component> getCustomRepository()
    {
        return customRepository;
    }
}
