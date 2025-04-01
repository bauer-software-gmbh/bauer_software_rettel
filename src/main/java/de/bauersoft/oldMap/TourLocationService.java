package de.bauersoft.oldMap;

import de.bauersoft.data.entities.tour.tour.Tour;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.repositories.tour.TourRepository;
import de.bauersoft.data.repositories.user.UserRepository;
import de.bauersoft.mobile.broadcaster.LocationBroadcaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TourLocationService {

    private final TourLocationRepository tourLocationRepository;
    private final TourRepository tourRepository;

    @Autowired
    public TourLocationService(TourLocationRepository tourLocationRepository, TourRepository tourRepository) {
        this.tourLocationRepository = tourLocationRepository;
        this.tourRepository = tourRepository;
    }

    public List<TourLocationDTO> getAllTourLocations() {
        return tourLocationRepository.findAllTourLocations();
    }

    public List<TourLocationDTO> getTourLocationsToday(Long tourId) {
        return tourLocationRepository.findTourLocationsToday(tourId);
    }

    public List<TourLocationDTO> getTourLocationsByDate(Long tourId, LocalDate date) {
        return tourLocationRepository.findTourLocationsByDate(tourId, date.atStartOfDay(), date.atTime(23, 59, 59));
    }

    public List<TourLocationDTO> getLatestTourLocationsByDate(LocalDate date) {
        return tourLocationRepository.findLatestTourLocationsByDate(date);
    }

    public String getTourNameById(Long tourId) {
        return tourRepository.findById(tourId)
                .map(Tour::getName)
                .orElse("Unbekannt");
    }

    public void saveLocation(TourLocationDTO dto) {
        Tour tour = tourRepository.findById(dto.getTourId())
                .orElseThrow(() -> new RuntimeException("Tour mit ID " + dto.getTourId() + " nicht gefunden"));

        TourLocation location = new TourLocation();
        location.setTour(tour);
        location.setLatitude(dto.getLatitude());
        location.setLongitude(dto.getLongitude());
        location.setTimestamp(dto.getTimestamp());

        tourLocationRepository.save(location);
        LocationBroadcaster.broadcastNewLocation(dto);
    }

    public String getDriverNameByTourId(Long tourId) {
        return tourRepository.findById(tourId)
                .map(t -> t.getDriver() != null ?
                        t.getDriver().getUser().getName() + " " + t.getDriver().getUser().getSurname()
                        : "Kein Fahrer")
                .orElse("Unbekannt");
    }

    public Object getCoDriverNameByTourId(Long tourId)
    {
        return tourRepository.findById(tourId)
                .map(t -> t.getCoDriver() != null ?
                        t.getCoDriver().getUser().getName() + " " + t.getCoDriver().getUser().getSurname()
                        : "Kein Beifahrer")
                .orElse("Unbekannt");
    }
}
