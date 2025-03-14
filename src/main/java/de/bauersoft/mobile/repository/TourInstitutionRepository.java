package de.bauersoft.mobile.repository;

import de.bauersoft.data.entities.tour.TourInstitution;
import de.bauersoft.data.entities.tour.TourInstitutionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TourInstitutionRepository extends JpaRepository<TourInstitution, TourInstitutionKey>, JpaSpecificationExecutor<TourInstitution>
{
    List<TourInstitution> findByTourId(Long tourId);
}

}
