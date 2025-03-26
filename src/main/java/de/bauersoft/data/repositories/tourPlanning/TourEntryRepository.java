package de.bauersoft.data.repositories.tourPlanning;

import de.bauersoft.data.entities.tourPlanning.tour.TourEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TourEntryRepository extends JpaRepository<TourEntry, Long> {

    List<TourEntry> findByDate(LocalDate date);

}


