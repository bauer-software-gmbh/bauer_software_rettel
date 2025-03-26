package de.bauersoft.data.repositories.tourPlanning;

import de.bauersoft.data.entities.tourPlanning.tour.TourInstitution;
import de.bauersoft.data.entities.tourPlanning.tour.TourInstitutionKey;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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
}
