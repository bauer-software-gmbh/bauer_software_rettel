package de.bauersoft.services.tour;

import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.tour.tour.LatLngPoint;
import de.bauersoft.data.entities.tour.tour.Tour;
import de.bauersoft.data.entities.tour.tour.TourLocation;
import de.bauersoft.data.repositories.institution.InstitutionRepository;
import de.bauersoft.data.repositories.tour.TourLocationRepository;
import de.bauersoft.data.repositories.tour.TourRepository;
import de.bauersoft.mobile.broadcaster.LocationBroadcaster;
import de.bauersoft.mobile.model.DTO.TourLocationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TourLocationService {

    private final TourLocationRepository tourLocationRepository;
    private final TourRepository tourRepository;
    private final InstitutionRepository institutionRepository;

    @Autowired
    public TourLocationService(TourLocationRepository tourLocationRepository, TourRepository tourRepository, InstitutionRepository institutionRepository) {
        this.tourLocationRepository = tourLocationRepository;
        this.tourRepository = tourRepository;
        this.institutionRepository = institutionRepository;
    }

    public List<TourLocationDTO> getAllTourLocations() {
        return tourLocationRepository.findAllTourLocations();
    }

    public List<TourLocationDTO> getTourLocationsToday(Long tourId) {
        return tourLocationRepository.findTourLocationsToday(tourId);
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

    public String getInstitutionIdByLonLatAndTourID(Double lon, Double lat, Long tourId, LocalDateTime time)
    {
        return tourLocationRepository.findInstitutionIdByLonLatAndTourID(lon, lat, tourId, time);
    }

    public void insertTourLocation(Double latitude, Double longitude, LocalDateTime localDateTime, Long tourId, Long institutId)
    {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour mit ID " + tourId + " nicht gefunden"));

        Institution institution = institutionRepository.findById(institutId)
                .orElseThrow(() -> new RuntimeException("Institution mit ID " + institutId + " nicht gefunden"));

        TourLocation location = new TourLocation();
        location.setTour(tour);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTimestamp(localDateTime);
        location.setInstitution(institution);
        location.setMarkerIcon("X"); // ✅ nicht vergessen!

        tourLocationRepository.save(location);

        // Broadcast vorbereiten
        TourLocationDTO dto = new TourLocationDTO();
        dto.setId(location.getId());
        dto.setTourId(tourId);
        dto.setLatitude(latitude);
        dto.setLongitude(longitude);
        dto.setTimestamp(localDateTime);
        dto.setMarkerIcon("X"); // ✅ für farbige Marker

        LocationBroadcaster.broadcastNewLocation(dto);
    }
}
