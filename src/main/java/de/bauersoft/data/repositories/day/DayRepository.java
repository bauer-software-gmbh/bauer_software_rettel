package de.bauersoft.data.repositories.day;

import de.bauersoft.data.entities.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface DayRepository extends JpaRepository<Day, Long> {
    @Query("SELECT d FROM Day d LEFT JOIN FETCH d.menus WHERE d.date = :date")
    Optional<Day> findByDate(LocalDate date);
}
