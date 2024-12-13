package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.Day;
import de.bauersoft.data.entities.Menu;
import de.bauersoft.data.entities.Recipe;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.day.DayGridDataRepository;
import de.bauersoft.data.repositories.day.DayRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DayService {

    private final DayRepository repository;
    private final DayGridDataRepository customRepository;

    public DayService(DayRepository repository, DayGridDataRepository customRepository) {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    public Day saveDay(Day day) {
        return repository.save(day);
    }

    public Optional<Day> findById(Long id) {
        return repository.findById(id);
    }

    public List<Day> findAll() {
        return repository.findAll();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public void addMenuToDay(Long dayId, Menu menu) {
        Day day = repository.findById(dayId).orElseThrow(() -> new RuntimeException("Tag nicht gefunden"));
        day.getMenus().add(menu);
        repository.save(day);
    }

    public Optional<Day> findByDate(LocalDate date) {
        return repository.findByDate(date);
    }

    public int count(List<SerializableFilter<Day, ?>> filter) {
        return (int) customRepository.count(filter);
    }

    public List<Day> fetchAll(List<SerializableFilter<Day, ?>> filters, List<QuerySortOrder> sortOrder) {
        return customRepository.fetchAll(filters,sortOrder);
    }
}

