package de.bauersoft.services.tourPlanning;

import de.bauersoft.data.entities.tourPlanning.tour.Tour;
import de.bauersoft.data.entities.tourPlanning.tour.TourEntry;
import de.bauersoft.data.entities.tourPlanning.tour.TourInformation;
import de.bauersoft.data.repositories.tourPlanning.TourEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
}