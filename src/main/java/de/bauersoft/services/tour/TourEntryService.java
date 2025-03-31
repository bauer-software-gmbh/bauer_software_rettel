package de.bauersoft.services.tour;

import de.bauersoft.data.entities.tour.tour.Tour;
import de.bauersoft.data.entities.tour.tour.TourEntry;
import de.bauersoft.data.repositories.tour.TourEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TourEntryService {

    private final TourEntryRepository repository;

    public TourEntryService(TourEntryRepository repository) {
        this.repository = repository;
    }

    public List<TourEntry> getByDate(LocalDate date) {
        return repository.findByDate(date);
    }

    public TourEntry saveEntry(Tour tour, LocalDate date) {
        TourEntry entry = new TourEntry();
        entry.setTour(tour);
        entry.setDate(date);
        return repository.save(entry);
    }

    public TourEntry update(TourEntry entry) {
        return repository.save(entry);
    }

    public void deleteEntry(Long id) {
        repository.deleteById(id);
    }

    public TourEntry save(TourEntry entry) {
        return repository.save(entry);
    }
}