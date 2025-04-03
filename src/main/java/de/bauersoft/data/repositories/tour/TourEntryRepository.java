package de.bauersoft.data.repositories.tour;

import de.bauersoft.data.entities.tour.tour.TourEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TourEntryRepository extends JpaRepository<TourEntry, Long> {

    List<TourEntry> findByDate(LocalDate date);

    TourEntry findByTourIdAndDate(Long tourId, LocalDate date);
}


