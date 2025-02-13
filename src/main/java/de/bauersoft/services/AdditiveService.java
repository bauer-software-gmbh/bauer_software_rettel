package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.additive.AdditiveGridDataRepository;
import de.bauersoft.data.repositories.additive.AdditiveRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class AdditiveService implements ServiceBase<Additive, Long>
{
    private final AdditiveRepository repository;
    private final AdditiveGridDataRepository customRepository;

    public AdditiveService(AdditiveRepository repository, AdditiveGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<Additive> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Additive update(Additive entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<Additive> updateAll(Collection<Additive> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(Additive entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<Additive> entities)
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
    public List<Additive> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<Additive> list(Pageable pageable)
    {
            return repository.findAll(pageable);
    }

    @Override
    public Page<Additive> list(Pageable pageable, Specification<Additive> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Additive, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<Additive> fetchAll(List<SerializableFilter<Additive, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public AdditiveRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Additive> getCustomRepository()
    {
        return customRepository;
    }
}
