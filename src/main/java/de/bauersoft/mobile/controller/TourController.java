package de.bauersoft.mobile.controller;

import de.bauersoft.data.entities.user.User;
import de.bauersoft.mobile.model.DummyTourData;
import de.bauersoft.mobile.model.DTO.TourDTO;
import de.bauersoft.mobile.security.JwtTokenProvider;
import de.bauersoft.services.tour.TourService;
import de.bauersoft.mobile.utils.AESUtil;
import de.bauersoft.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/mobile/tours")
@CrossOrigin(origins = "*")
public class TourController {
    private static final Logger logger = LoggerFactory.getLogger(TourController.class);

    private final TourService tourService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AESUtil aesUtil;

    public TourController(TourService tourService, UserService userService, JwtTokenProvider jwtTokenProvider, AESUtil aesUtil) {
        this.tourService = tourService;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.aesUtil = aesUtil;
    }

    @GetMapping("/today")
    public ResponseEntity<List<TourDTO>> getTodayToursForDriver(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "username", required = false) String encryptedUsername)
    {

        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");
            username = jwtTokenProvider.getUsernameFromToken(token);
            logger.info("🔐 Benutzername aus Token extrahiert: {}", username);
        } else if (encryptedUsername != null) {
            try {
                username = aesUtil.decrypt(encryptedUsername);
                logger.info("📩 Entschlüsselter Benutzername aus Request: {}", username);
            } catch (Exception e) {
                logger.error("❌ Fehler beim Entschlüsseln des Benutzernamens", e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }

        User tempUser = userService.findUserByEmail(username);
        Long userId = tempUser.getId();
        logger.info("👤 User ID: {}", userId);

        List<TourDTO> tours = tourService.getToursForDriverAndDate(userId);

        logger.info("🚚 Gefundene Touren für {}: {}", username, tours.size());

        return ResponseEntity.ok(tours);
    }

}
