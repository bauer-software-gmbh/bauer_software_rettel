package de.bauersoft.mobile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.bauersoft.mobile.model.LoginRequest;
import de.bauersoft.mobile.security.JwtTokenProvider;
import de.bauersoft.mobile.utils.AESUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller für die Authentifizierung und Token-Generierung.
 * <p>
 * Dieser Controller bietet eine API für das mobile Login und testet verschlüsselte Requests.
 * </p>
 */
@AnonymousAllowed
@RestController()
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final AESUtil aesUtil;

    /**
     * Konstruktor für `AuthController`, der die benötigten Abhängigkeiten injiziert.
     *
     * @param authenticationManager Der Spring Security `AuthenticationManager`.
     * @param jwtTokenProvider      Der JWT-Token-Provider für die Token-Erstellung.
     * @param userDetailsService    Der Service zur Benutzerverwaltung.
     * @param aesUtil               Utility-Klasse zur AES-Verschlüsselung.
     */
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService, AESUtil aesUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.aesUtil = aesUtil;
    }

    /**
     * Test-Endpunkt zum Überprüfen der Verschlüsselung.
     *
     * @param data Die verschlüsselten Test-Daten.
     * @return Die empfangenen Daten als Antwort.
     */
    @PostMapping("api/mobile/test")
    public ResponseEntity<?> test(@RequestBody String data) {
        logger.info("📩 Test-Request erhalten: {}", data);
        return ResponseEntity.ok(data);
    }

    /**
     * Endpunkt für den mobilen Login.
     * <p>
     * Diese Methode:
     * 1. Entschlüsselt die übermittelten Anmeldedaten.
     * 2. Authentifiziert den Benutzer mit Spring Security.
     * 3. Erstellt ein JWT-Token für den Benutzer.
     * 4. Gibt das Token und Benutzerinformationen als Antwort zurück.
     * </p>
     *
     * @param requestData Die verschlüsselten Anmeldedaten als String.
     * @return Ein `ResponseEntity` mit dem JWT-Token und Account-Infos oder einer Fehlerantwort.
     */
    @PostMapping("api/mobile/login")
    public ResponseEntity<?> login(@RequestBody String requestData) {
        logger.info("🔒 Login-Anfrage erhalten");

        try {
            logger.debug("📩 Verschlüsselte Daten empfangen: {}", requestData);

            // Entschlüsselung der Daten
            String decryptedData = aesUtil.decrypt(requestData);
            logger.debug("🔓 Entschlüsselte Daten: {}", decryptedData);

            // JSON in LoginRequest umwandeln
            ObjectMapper objectMapper = new ObjectMapper();
            LoginRequest loginRequest = objectMapper.readValue(decryptedData, LoginRequest.class);
            logger.info("👤 Benutzername erhalten: {}", loginRequest.username());

            // Benutzer authentifizieren
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(), loginRequest.password()
                    )
            );

            logger.info("✅ Authentifizierung erfolgreich für Benutzer: {}", loginRequest.username());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Benutzer aus Datenbank abrufen
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.username());
            logger.debug("👤 Benutzer gefunden: {}", userDetails.getUsername());

            // JWT-Token generieren
            String auth_token = jwtTokenProvider.generateToken(userDetails);
            logger.info("🔑 JWT-Token generiert für Benutzer: {}", loginRequest.username());

            // Account-Info hinzufügen (optional)
            Map<String, Object> accountInfo = new HashMap<>();
            accountInfo.put("user", loginRequest.username());

            // Antwort mit Token + Benutzerinformationen
            Map<String, Object> response = new HashMap<>();
            response.put("auth_token", auth_token);
            response.put("account_info", accountInfo);

            logger.info("📤 Login-Erfolg - Token wird zurückgegeben.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ Fehler beim Login für Anfrage: {} - {}", requestData, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
