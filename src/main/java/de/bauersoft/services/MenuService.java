package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.Day;
import de.bauersoft.data.entities.Menu;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.day.DayRepository;
import de.bauersoft.data.repositories.menu.MenuGridDataRepository;
import de.bauersoft.data.repositories.menu.MenuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuService
{

    private final MenuRepository repository;
    private final DayRepository dayRepository;
    private final MenuGridDataRepository gridRepository;

    public MenuService(MenuRepository repository, DayRepository dayRepository, MenuGridDataRepository gridRepository) {
        this.repository = repository;
        this.dayRepository = dayRepository;

        this.gridRepository = gridRepository;
    }

    public Menu saveMenu(Menu menu) {
        return repository.save(menu);
    }

    public Optional<Menu> findById(Long id) {
        return repository.findById(id);
    }

    public List<Menu> findAll() {
        return repository.findAll();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public void addMenuToDay(Day day, Menu menu) {
        // Stelle sicher, dass das Menü und der Tag existieren
        if (!day.getMenus().contains(menu)) {
            // Füge das Menü zum Tag hinzu
            day.addMenu(menu);
            menu.setDays(List.of(day));  // Füge den Tag zum

            // Speichere den Tag mit dem hinzugefügten Menü
            dayRepository.save(day);  // Dies speichert den Tag und aktualisiert die Many-to-Many-Beziehung in der DB
        }
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

