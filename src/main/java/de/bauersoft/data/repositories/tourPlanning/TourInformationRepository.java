package de.bauersoft.data.repositories.tourPlanning;

import de.bauersoft.data.entities.tourPlanning.tour.TourInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourInformationRepository extends JpaRepository<TourInformation, Long>
{
}
