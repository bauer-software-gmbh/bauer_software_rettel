package de.bauersoft.services;

import de.bauersoft.data.entities.Day;
import de.bauersoft.data.entities.Menu;
import de.bauersoft.data.repositories.day.DayRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DayService {

    private final DayRepository repository;

    public DayService(DayRepository repository) {
        this.repository = repository;
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
}

