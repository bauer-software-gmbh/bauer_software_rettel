package de.bauersoft.data.repositories.user;

import de.bauersoft.data.entities.tour.tour.TourLocation;
import de.bauersoft.mobile.model.DTO.UserLocationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserLocationRepository extends JpaRepository<TourLocation, Long> {

    @Query("SELECT new de.bauersoft.mobile.model.DTO.UserLocationDTO(" +
            "u.id, u.latitude, u.longitude, u.timestamp, u.user.id) " +
            "FROM TourLocation u")
    List<UserLocationDTO> findAllUserLocations();


    @Query("SELECT new de.bauersoft.mobile.model.DTO.UserLocationDTO(u.id, u.latitude, u.longitude, u.timestamp, u.user.id) " +
            "FROM TourLocation u " +
            "WHERE u.user.id = :userId AND DATE(u.timestamp) = CURRENT_DATE")
    List<UserLocationDTO> findUserLocationsToday(@Param("userId") Long userId);

    @Query("SELECT new de.bauersoft.mobile.model.DTO.UserLocationDTO(u.id, u.latitude, u.longitude, u.timestamp, u.user.id) " +
            "FROM TourLocation u " +
            "WHERE u.user.id = :userId " +
            "AND u.timestamp BETWEEN :selectedDateStart AND :selectedDateEnd")
    List<UserLocationDTO> findUserLocationsByDate(@Param("userId") Long userId,
                                                  @Param("selectedDateStart") LocalDateTime selectedDateStart,
                                                  @Param("selectedDateEnd") LocalDateTime selectedDateEnd);


    @Query("SELECT new de.bauersoft.mobile.model.DTO.UserLocationDTO(u.id, u.latitude, u.longitude, u.timestamp, u.user.id) " +
            "FROM TourLocation u " +
            "WHERE FUNCTION('DATE', u.timestamp) = :selectedDate " +
            "AND u.timestamp = (SELECT MAX(ul.timestamp) FROM TourLocation ul WHERE ul.user.id = u.user.id AND FUNCTION('DATE', ul.timestamp) = :selectedDate)")
    List<UserLocationDTO> findLatestUserLocationsByDate(@Param("selectedDate") LocalDate selectedDate);

}

