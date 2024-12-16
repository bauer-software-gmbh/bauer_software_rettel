package de.bauersoft.services;

import de.bauersoft.data.entities.Day;
import de.bauersoft.data.entities.Menu;
import de.bauersoft.data.repositories.day.DayRepository;
import de.bauersoft.data.repositories.menu.MenuRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuService {

    private final MenuRepository repository;
    private final DayRepository dayRepository;

    public MenuService(MenuRepository repository, DayRepository dayRepository) {
        this.repository = repository;
        this.dayRepository = dayRepository;
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

}

