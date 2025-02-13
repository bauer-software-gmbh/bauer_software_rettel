package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.menu.MenuGridDataRepository;
import de.bauersoft.data.repositories.menu.MenuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class MenuService implements ServiceBase<Menu, Long>
{
    private final MenuRepository repository;
    private final MenuGridDataRepository customRepository;

    public MenuService(MenuRepository repository, MenuGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<Menu> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Menu update(Menu entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<Menu> updateAll(Collection<Menu> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(Menu entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<Menu> entities)
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
    public List<Menu> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<Menu> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Menu> list(Pageable pageable, Specification<Menu> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Menu, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<Menu> fetchAll(List<SerializableFilter<Menu, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public MenuRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Menu> getCustomRepository()
    {
        return customRepository;
    }
}
