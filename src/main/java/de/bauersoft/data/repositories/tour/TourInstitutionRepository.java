package de.bauersoft.data.repositories.tour;

import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.tour.tour.TourInstitution;
import de.bauersoft.data.entities.tour.tour.TourInstitutionKey;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TourInstitutionRepository extends JpaRepository<TourInstitution, TourInstitutionKey>, JpaSpecificationExecutor<TourInstitution>
{
    List<TourInstitution> findAllByTour_Id(Long id);

    Optional<TourInstitution> findByTour_IdAndInstitution_Id(Long id, Long institutionId);

    @Modifying
    @Transactional
    @Query("""
            delete from TourInstitution ti
            where ti.id = :id
    """)
    void deleteById(TourInstitutionKey id);

    List<TourInstitution> findByTourId(Long id);

    @Query("""
        select i from Institution i
        where i.id not in (
            select ti.institution.id from TourInstitution ti
            where ti.tour.holidayMode = :holidayMode
        )
    """)
    List<Institution> findAllUnplannedInstitutions(boolean holidayMode);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE tour_institutions 
        SET temperature = :temperature, validation_date_time = :timestamp
        WHERE tour_id = :tourId AND institution_id = :institutId
    """, nativeQuery = true)
    void updateTemperatureByTourIdAndInstitutionsId(
            @Param("temperature") Number temperature,
            @Param("timestamp") LocalDateTime timestamp,
            @Param("tourId") Long tourId,
            @Param("institutId") Long institutId);


}
