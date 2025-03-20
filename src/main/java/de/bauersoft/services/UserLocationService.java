package de.bauersoft.services;

import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.entities.user.UserLocation;
import de.bauersoft.data.repositories.user.UserLocationRepository;
import de.bauersoft.data.repositories.user.UserRepository;
import de.bauersoft.mobile.model.DTO.UserLocationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserLocationService {

    private final UserLocationRepository userLocationRepository;
    @Autowired
    private UserRepository userRepository; // Füge das Repository für User hinzu

    public UserLocationService(UserLocationRepository userLocationRepository) {
        this.userLocationRepository = userLocationRepository;
    }

    public List<UserLocationDTO> getAllUserLocations() {
        return userLocationRepository.findAllUserLocations();
    }

    public UserLocationDTO saveUserLocation(UserLocationDTO locationDTO) {
        User user = userRepository.findById(locationDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("❌ User mit ID " + locationDTO.getUserId() + " nicht gefunden!"));

        // ✅ NEU: Jedes Mal ein neuer Standort speichern!
        UserLocation location = new UserLocation();
        location.setUser(user);
        location.setLatitude(locationDTO.getLatitude());
        location.setLongitude(locationDTO.getLongitude());
        location.setTimestamp(locationDTO.getTimestamp());

        UserLocation savedLocation = userLocationRepository.save(location);

        return new UserLocationDTO(
                savedLocation.getId(),
                savedLocation.getLatitude(),
                savedLocation.getLongitude(),
                savedLocation.getTimestamp(),
                savedLocation.getUser().getId()
        );
    }

    public List<UserLocationDTO> getUserLocationsToday(Long userId) {
        return userLocationRepository.findUserLocationsToday(userId);
    }

    public List<UserLocationDTO> getUserLocationsByDate(Long userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay(); // 00:00:00 des Tages
        LocalDateTime endOfDay = date.atTime(23, 59, 59); // 23:59:59 des Tages
        return userLocationRepository.findUserLocationsByDate(userId, startOfDay, endOfDay);
    }

    public List<UserLocationDTO> getLatestUserLocationsByDate(LocalDate date) {
        return userLocationRepository.findLatestUserLocationsByDate(date);
    }

    public String getFullNameByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getName() + " " + user.getSurname())
                .orElse("Unbekannt"); // Falls User nicht existiert
    }
}

