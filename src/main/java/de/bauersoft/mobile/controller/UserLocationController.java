package de.bauersoft.mobile.controller;

import de.bauersoft.data.repositories.user.UserLocationRepository;
import de.bauersoft.mobile.model.DTO.UserLocationDTO;
import de.bauersoft.services.UserLocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mobile/locations")
public class UserLocationController {

    private final UserLocationService userLocationService;

    public UserLocationController(UserLocationService userLocationService)
    {
        this.userLocationService = userLocationService;
    }

    @PostMapping
    public ResponseEntity<Void> saveUserLocation(@RequestBody UserLocationDTO locationDTO) {
        userLocationService.saveLocation(locationDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<Void> saveUserLocations(@RequestBody List<UserLocationDTO> locations) {
        locations.forEach(userLocationService::saveLocation);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/today/{userId}")
    public ResponseEntity<List<UserLocationDTO>> getUserLocationsToday(@PathVariable Long userId) {
        List<UserLocationDTO> locations = userLocationService.getUserLocationsToday(userId);
        return ResponseEntity.ok(locations);
    }

    @GetMapping
    public ResponseEntity<List<UserLocationDTO>> getAllUserLocations() {
        List<UserLocationDTO> locations = userLocationService.getAllUserLocations();
        return ResponseEntity.ok(locations);
    }
}


