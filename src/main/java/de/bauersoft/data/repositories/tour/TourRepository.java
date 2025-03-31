package de.bauersoft.data.repositories.tour;

import de.bauersoft.data.entities.tour.tour.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour>
{
    @Query("""
    SELECT t FROM Tour t
    JOIN FETCH t.driver d
    JOIN FETCH d.user du
    LEFT JOIN FETCH t.coDriver cd
    LEFT JOIN FETCH cd.user cu
    LEFT JOIN FETCH t.vehicle v
    LEFT JOIN FETCH t.institutions ti
    LEFT JOIN FETCH ti.institution i
    LEFT JOIN FETCH i.address a
    WHERE (t.driver.user.id = :userId OR t.coDriver.user.id = :userId)
    AND t.startDateTime BETWEEN :start AND :end
""")
    List<Tour> findToursByUserIdAndDate(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
