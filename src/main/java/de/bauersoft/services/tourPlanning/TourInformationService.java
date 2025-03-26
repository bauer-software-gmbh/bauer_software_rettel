package de.bauersoft.services.tourPlanning;

import de.bauersoft.data.entities.tourPlanning.tour.TourInformation;
import de.bauersoft.data.repositories.tourPlanning.TourInformationRepository;
import org.springframework.stereotype.Service;

@Service
public class TourInformationService {

    private final TourInformationRepository repo;

    public TourInformationService(TourInformationRepository repo) {
        this.repo = repo;
    }

    public TourInformation save(TourInformation info) {
        return repo.save(info);
    }
}
