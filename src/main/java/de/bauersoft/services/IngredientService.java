package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.entities.ingredient.Ingredient;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.ingredient.IngredientGridDataRepository;
import de.bauersoft.data.repositories.ingredient.IngredientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class IngredientService implements ServiceBase<Ingredient, Long>
{
    private final IngredientRepository repository;
    private final IngredientGridDataRepository customRepository;

    public IngredientService(IngredientRepository repository, IngredientGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<Ingredient> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Ingredient update(Ingredient entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<Ingredient> updateAll(Collection<Ingredient> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(Ingredient entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<Ingredient> entities)
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
    public List<Ingredient> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<Ingredient> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Ingredient> list(Pageable pageable, Specification<Ingredient> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Ingredient, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<Ingredient> fetchAll(List<SerializableFilter<Ingredient, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public IngredientRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Ingredient> getCustomRepository()
    {
        return customRepository;
    }
}
