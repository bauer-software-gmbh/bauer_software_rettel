package de.bauersoft.mobile.controller;

import de.bauersoft.data.entities.user.User;
//import de.bauersoft.mobile.model.DummyTourData;
import de.bauersoft.mobile.model.DTO.TourDTO;
import de.bauersoft.mobile.security.JwtTokenProvider;
import de.bauersoft.services.tour.TourInstitutionService;
import de.bauersoft.services.tour.TourLocationService;
import de.bauersoft.services.tour.TourService;
import de.bauersoft.mobile.utils.AESUtil;
import de.bauersoft.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final TourInstitutionService tourInstitutionService;
    private final TourLocationService tourLocationService;

    public TourController(TourService tourService, UserService userService, JwtTokenProvider jwtTokenProvider, AESUtil aesUtil, TourInstitutionService tourInstitutionService, TourLocationService tourLocationService) {
        this.tourService = tourService;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.aesUtil = aesUtil;
        this.tourInstitutionService = tourInstitutionService;
        this.tourLocationService = tourLocationService;
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

    @GetMapping("/today/time")
    public ResponseEntity postTodayToursTime(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "username", required = false) String encryptedUsername,
            @RequestParam(value = "tourId", required = false) Long tourId,
            @RequestParam(value = "start", required = false) boolean start,
            @RequestParam(value = "timestamp", required = false) Long timestamp)
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
        logger.info("🔍 Timestamp : " + timestamp + ", UserID : " + userId + ", TourID : " + tourId + ", Start : " + start);

        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()); // oder ZoneId.of("Europe/Berlin")

        if (start) {
            tourService.updateStartTourByDriverIdAndTourId(userId, tourId, localDateTime);
            logger.info("✅ StartDateTime für Tour " + tourId + " geupdatet");
        } else {
            tourService.updateEndTourByDriverIdAndTourId(userId, tourId, localDateTime);
            logger.info("✅ EndDateTime für Tour " + tourId + " geupdatet");
        }


        return ResponseEntity.ok().build();
    }

    @GetMapping("/today/temp")
    public ResponseEntity postTodayTourInstitutTemp(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "username", required = false) String encryptedUsername,
            @RequestParam(value = "tourId", required = false) Long tourId,
            @RequestParam(value = "institutId", required = false) Long institutId,
            @RequestParam(value = "temperature", required = false) Long temperature,
            @RequestParam(value = "timestamp", required = false) Long timestamp,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude)
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

        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()); // oder ZoneId.of("Europe/Berlin")

        logger.info("🔍 Latidude : " + latitude + ", Longitude : " + longitude + ", UserID : " + userId + ", TourID : " + tourId + ", InstitutID : " + institutId);

        tourInstitutionService.updateTemperatureByTourIdAndInstitutionsId(temperature, localDateTime, tourId, institutId);
        tourLocationService.insertTourLocation(latitude, longitude, localDateTime, tourId, institutId);
        logger.info("✅ Temperatur und Standort für Tour " + tourId + " geupdatet");

        return ResponseEntity.ok().build();
    }

}
