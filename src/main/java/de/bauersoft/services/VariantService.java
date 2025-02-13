package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.variant.Variant;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.variant.VariantGridDataRepository;
import de.bauersoft.data.repositories.variant.VariantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class VariantService implements ServiceBase<Variant, Long>
{
    private final VariantRepository repository;
    private final VariantGridDataRepository customRepository;

    public VariantService(VariantRepository repository, VariantGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<Variant> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Variant update(Variant entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<Variant> updateAll(Collection<Variant> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(Variant entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<Variant> entities)
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
    public List<Variant> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<Variant> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Variant> list(Pageable pageable, Specification<Variant> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Variant, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<Variant> fetchAll(List<SerializableFilter<Variant, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public VariantRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Variant> getCustomRepository()
    {
        return customRepository;
    }

    public void deleteAllByMenuId(Long menuId)
    {
        repository.deleteAllByMenuId(menuId);
    }

    public List<Variant> findAllByMenuId(Long menuId)
    {
        return repository.findAllByMenuId(menuId);
    }

    public void updateVariants(List<Variant> oldVariants, List<Variant> newVariants)
    {
        if(oldVariants == null)
            oldVariants = new ArrayList<>();

        if(newVariants == null)
            newVariants = new ArrayList<>();

        List<Variant> remove = new ArrayList<>();
        List<Variant> update = new ArrayList<>(newVariants);

        for(Variant oldVariant : oldVariants)
        {
            if(!newVariants.contains(oldVariant))
                remove.add(oldVariant);
        }

        repository.deleteAll(remove);
        repository.saveAll(update);
    }
}
