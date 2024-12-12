package de.bauersoft.services;

import de.bauersoft.data.entities.Menu;
import de.bauersoft.data.repositories.menu.MenuRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuService {

    private final MenuRepository repository;

    public MenuService(MenuRepository repository) {
        this.repository = repository;
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

}

