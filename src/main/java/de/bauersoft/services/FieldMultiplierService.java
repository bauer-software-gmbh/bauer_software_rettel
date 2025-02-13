package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.entities.field.FieldMultiplier;
import de.bauersoft.data.entities.field.FieldMultiplierKey;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.field.FieldMultiplierRepository;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class FieldMultiplierService implements ServiceBase<FieldMultiplier, FieldMultiplierKey>
{
    private final FieldMultiplierRepository repository;

    public FieldMultiplierService(FieldMultiplierRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public Optional<FieldMultiplier> get(FieldMultiplierKey fieldMultiplierKey)
    {
        return repository.findById(fieldMultiplierKey);
    }

    @Override
    public FieldMultiplier update(FieldMultiplier entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<FieldMultiplier> updateAll(Collection<FieldMultiplier> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(FieldMultiplier entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(FieldMultiplierKey fieldMultiplierKey)
    {
        repository.deleteById(fieldMultiplierKey);
    }

    @Override
    public void deleteAll(Collection<FieldMultiplier> entities)
    {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll()
    {
        repository.deleteAll();
    }

    @Override
    public void deleteAllById(Collection<FieldMultiplierKey> fieldMultiplierKeys)
    {
        repository.deleteAllById(fieldMultiplierKeys);
    }

    @Override
    public List<FieldMultiplier> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<FieldMultiplier> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<FieldMultiplier> list(Pageable pageable, Specification<FieldMultiplier> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<FieldMultiplier, ?>> serializableFilters)
    {
        return 0;
    }

    @Override
    public List<FieldMultiplier> fetchAll(List<SerializableFilter<FieldMultiplier, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return List.of();
    }

    @Override
    public FieldMultiplierRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<FieldMultiplier> getCustomRepository()
    {
        return null;
    }

    public void deleteAllByFieldId(Long fieldId)
    {
        repository.deleteAllByFieldId(fieldId);
    }

}
