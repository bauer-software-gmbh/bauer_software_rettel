package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.unit.Unit;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.unit.UnitGridDataRepository;
import de.bauersoft.data.repositories.unit.UnitRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UnitService implements ServiceBase<Unit, Long>
{
    private final UnitRepository repository;
    private final UnitGridDataRepository customRepository;

    public UnitService(UnitRepository repository, UnitGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<Unit> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Unit update(Unit entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<Unit> updateAll(Collection<Unit> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(Unit entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<Unit> entities)
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
    public List<Unit> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<Unit> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Unit> list(Pageable pageable, Specification<Unit> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Unit, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<Unit> fetchAll(List<SerializableFilter<Unit, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public UnitRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Unit> getCustomRepository()
    {
        return customRepository;
    }

    public List<Unit> findAllByName(Pageable pageable, String name)
    {
        return repository.findAllByName(pageable, name);
    }

    public int countAllByName(String name)
    {
        return repository.countAllByName(name);
    }

    public Set<Unit> findAllByShorthand(String shorthand)
    {
        return repository.findAllByShorthand(shorthand);
    }

    public Set<Unit> findAllByParentUnitName(String parentUnitName)
    {
        return repository.findAllByParentUnitName(parentUnitName);
    }

    public Set<Unit> findAllByParentFactor(float parentFactor)
    {
        return repository.findAllByParentFactor(parentFactor);
    }
}
