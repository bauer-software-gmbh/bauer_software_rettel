package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.entities.flesh.Flesh;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.flesh.FleshGridDataRepository;
import de.bauersoft.data.repositories.flesh.FleshRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class FleshService implements ServiceBase<Flesh, Long>
{
    private final FleshRepository repository;
    private final FleshGridDataRepository customRepository;

    public FleshService(FleshRepository repository, FleshGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<Flesh> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Flesh update(Flesh entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<Flesh> updateAll(Collection<Flesh> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(Flesh entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<Flesh> entities)
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
    public List<Flesh> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<Flesh> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Flesh> list(Pageable pageable, Specification<Flesh> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Flesh, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<Flesh> fetchAll(List<SerializableFilter<Flesh, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public FleshRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Flesh> getCustomRepository()
    {
        return customRepository;
    }
}
