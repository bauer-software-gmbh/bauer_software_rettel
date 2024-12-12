package de.bauersoft.data.repositories.day;

import de.bauersoft.data.entities.Day;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DayRepository extends JpaRepository<Day, Long> {
    Optional<Day> findByDate(LocalDate date);
}
