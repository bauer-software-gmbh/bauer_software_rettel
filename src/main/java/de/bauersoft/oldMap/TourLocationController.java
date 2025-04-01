package de.bauersoft.oldMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mobile/tourlocations")
public class TourLocationController {

    private final TourLocationService service;

    public TourLocationController(TourLocationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> saveLocation(@RequestBody TourLocationDTO dto) {
        service.saveLocation(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<Void> saveBatch(@RequestBody List<TourLocationDTO> dtos) {
        dtos.forEach(service::saveLocation);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/today/{tourId}")
    public ResponseEntity<List<TourLocationDTO>> getToday(@PathVariable Long tourId) {
        return ResponseEntity.ok(service.getTourLocationsToday(tourId));
    }

    @GetMapping("/today")
    public ResponseEntity<List<TourLocationDTO>> getAll() {
        return ResponseEntity.ok(service.getAllTourLocations());
    }
}