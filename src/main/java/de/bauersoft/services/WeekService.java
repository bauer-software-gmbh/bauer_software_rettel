package de.bauersoft.services;

import de.bauersoft.data.entities.Week;
import de.bauersoft.data.repositories.week.WeekRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WeekService {
    private final WeekRepository repository;

    public WeekService(WeekRepository weekRepository) {
        this.repository = weekRepository;
    }

    public Week saveWeek(Week week) {
        return repository.save(week);
    }

    public Optional<Week> findById(Long id) {
        return repository.findById((id));
    }

    public List<Week> findAll() {
        return repository.findAll();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
