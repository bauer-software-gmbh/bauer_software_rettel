package de.bauersoft.data.repositories.tour;

import de.bauersoft.data.entities.tour.tour.TourLocation;
import de.bauersoft.mobile.model.DTO.TourLocationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TourLocationRepository extends JpaRepository<TourLocation, Long> {

    @Query("SELECT new de.bauersoft.mobile.model.DTO.TourLocationDTO(t.id, t.latitude, t.longitude, t.timestamp, t.tour.id) FROM TourLocation t")
    List<TourLocationDTO> findAllTourLocations();

    @Query("SELECT new de.bauersoft.mobile.model.DTO.TourLocationDTO(t.id, t.latitude, t.longitude, t.timestamp, t.tour.id) FROM TourLocation t WHERE t.tour.id = :tourId AND DATE(t.timestamp) = CURRENT_DATE")
    List<TourLocationDTO> findTourLocationsToday(@Param("tourId") Long tourId);

    @Query("SELECT new de.bauersoft.mobile.model.DTO.TourLocationDTO(t.id, t.latitude, t.longitude, t.timestamp, t.tour.id) FROM TourLocation t WHERE t.tour.id = :tourId AND t.timestamp BETWEEN :start AND :end")
    List<TourLocationDTO> findTourLocationsByDate(@Param("tourId") Long tourId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
    SELECT new de.bauersoft.mobile.model.DTO.TourLocationDTO(t.id, t.latitude, t.longitude, t.timestamp, t.tour.id) FROM TourLocation t WHERE FUNCTION('DATE', t.timestamp) = :selectedDate AND t.timestamp = (SELECT MAX(t2.timestamp) FROM TourLocation t2 WHERE t2.tour.id = t.tour.id AND FUNCTION('DATE', t2.timestamp) = :selectedDate)""")
    List<TourLocationDTO> findLatestTourLocationsByDate(@Param("selectedDate") LocalDate selectedDate);

}

