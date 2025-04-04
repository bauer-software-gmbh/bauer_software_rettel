package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.recipe.Recipe;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.recipe.RecipeGridDataRepository;
import de.bauersoft.data.repositories.recipe.RecipeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService implements ServiceBase<Recipe, Long>
{
    private final RecipeRepository repository;
    private final RecipeGridDataRepository customRepository;

    public RecipeService(RecipeRepository repository, RecipeGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<Recipe> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Recipe update(Recipe entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<Recipe> updateAll(Collection<Recipe> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(Recipe entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<Recipe> entities)
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
    public List<Recipe> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<Recipe> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Recipe> list(Pageable pageable, Specification<Recipe> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Recipe, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<Recipe> fetchAll(List<SerializableFilter<Recipe, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public RecipeRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Recipe> getCustomRepository()
    {
        return customRepository;
    }
}
