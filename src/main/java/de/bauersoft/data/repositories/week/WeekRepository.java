package de.bauersoft.data.repositories.week;

import de.bauersoft.data.entities.Week;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeekRepository extends JpaRepository<Week, Long> {
    Optional<Week> getWeekIdByKwAndYear(int kw, int year);
}
