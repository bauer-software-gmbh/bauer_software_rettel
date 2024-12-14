package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.menu.MenuGridDataRepository;
import de.bauersoft.data.repositories.menu.MenuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MenuService
{

    private final MenuRepository repository;
    private final MenuGridDataRepository gridRepository;

    public MenuService(MenuRepository repository, MenuGridDataRepository gridRepository)
    {
        Objects.requireNonNull(repository, "repository cannot be null.");
        Objects.requireNonNull(gridRepository, "gridRepository cannot be null.");

        this.repository = repository;
        this.gridRepository = gridRepository;
    }

    public Optional<Menu> get(Long id)
    {
        return repository.findById(id);
    }

    public Menu update(Menu entity)
    {
        return repository.save(entity);
    }

    public void delete(Long id)
    {
        repository.deleteById(id);
    }

    public Page<Menu> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    public Page<Menu> list(Pageable pageable, Specification<Menu> filter)
    {
        return repository.findAll(filter, pageable);
    }

    public int count()
    {
        return (int) repository.count();
    }

    public int count(List<SerializableFilter<Menu, ?>> filters)
    {
        return (int) gridRepository.count(filters);
    }

    public List<Menu> fetchAll(List<SerializableFilter<Menu, ?>> filters, List<QuerySortOrder> sortOrders)
    {
        return gridRepository.fetchAll(filters, sortOrders);
    }
}
