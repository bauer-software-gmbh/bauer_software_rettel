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

import java.util.List;
import java.util.Optional;

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
    public void delete(Long id)
    {
        repository.deleteById(id);
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
}
