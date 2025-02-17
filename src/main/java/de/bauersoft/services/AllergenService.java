package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.allergen.AllergenGridDataRepository;
import de.bauersoft.data.repositories.allergen.AllergenRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class AllergenService implements ServiceBase<Allergen, Long>
{
    private final AllergenRepository repository;
    private final AllergenGridDataRepository customRepository;

    public AllergenService(AllergenRepository repository, AllergenGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<Allergen> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Allergen update(Allergen entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<Allergen> updateAll(Collection<Allergen> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(Allergen entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<Allergen> entities)
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
    public List<Allergen> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<Allergen> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Allergen> list(Pageable pageable, Specification<Allergen> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Allergen, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<Allergen> fetchAll(List<SerializableFilter<Allergen, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public AllergenRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Allergen> getCustomRepository()
    {
        return customRepository;
    }
}
