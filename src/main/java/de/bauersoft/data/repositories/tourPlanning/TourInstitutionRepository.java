package de.bauersoft.data.repositories.tourPlanning;

import de.bauersoft.data.entities.tourPlanning.tour.TourInstitution;
import de.bauersoft.data.entities.tourPlanning.tour.TourInstitutionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TourInstitutionRepository extends JpaRepository<TourInstitution, TourInstitutionKey>, JpaSpecificationExecutor<TourInstitution>
{
    List<TourInstitution> findByTourId(Long tourId);
}
