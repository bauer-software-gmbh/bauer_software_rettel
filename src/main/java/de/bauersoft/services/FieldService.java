package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.field.FieldGridDataRepository;
import de.bauersoft.data.repositories.field.FieldRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class FieldService implements ServiceBase<Field, Long> 
{
    
    private final FieldRepository repository;
    private final FieldGridDataRepository customRepository;

    public FieldService(FieldRepository repository, FieldGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<Field> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Field update(Field entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<Field> updateAll(Collection<Field> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(Field entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<Field> entities)
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
    public List<Field> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<Field> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Field> list(Pageable pageable, Specification<Field> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Field, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<Field> fetchAll(List<SerializableFilter<Field, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public FieldRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Field> getCustomRepository()
    {
        return customRepository;
    }
}
