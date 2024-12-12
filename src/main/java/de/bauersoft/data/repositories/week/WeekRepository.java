package de.bauersoft.data.repositories.week;

import de.bauersoft.data.entities.Week;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeekRepository extends JpaRepository<Week, Long> {
}
