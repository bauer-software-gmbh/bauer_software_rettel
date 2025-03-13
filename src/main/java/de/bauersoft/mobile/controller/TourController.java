package de.bauersoft.mobile.controller;

import de.bauersoft.data.entities.tour.Tour;
import de.bauersoft.data.model.TourDTO;
import de.bauersoft.services.TourService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/mobile/tours")
@CrossOrigin(origins = "*")
public class TourController {
    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return Long.parseLong(userDetails.getUsername());
        }
        return null;
    }

    @GetMapping("/today")
    public ResponseEntity<List<TourDTO>> getTodayToursForDriver() {
        Long userId = 3L; // Testweise, später über `getCurrentUserId()`
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();

        List<TourDTO> tours = tourService.getToursForDriverAndDate(userId, start, end);
        return ResponseEntity.ok(tours);
    }
}
