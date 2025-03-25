package de.bauersoft.data.repositories.tourPlanning;

import de.bauersoft.data.entities.tourPlanning.tour.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour>
{
}
