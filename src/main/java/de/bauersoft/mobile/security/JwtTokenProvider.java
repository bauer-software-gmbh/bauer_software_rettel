package de.bauersoft.mobile.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Utility-Klasse zur Verwaltung von JWT-Token.
 * <p>
 * Diese Klasse enthält Methoden zum Generieren, Validieren und Extrahieren von Informationen
 * aus JSON Web Tokens (JWT).
 * </p>
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Erstellt den geheimen Schlüssel für die JWT-Signatur.
     *
     * @return Der generierte `Key` für die JWT-Signierung.
     */
    private Key getSigningKey() {
        logger.debug("🔑 Generiere JWT-Signatur-Schlüssel...");
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Erstellt ein neues JWT-Token für den angegebenen Benutzer.
     *
     * @param userDetails Die Benutzerinformationen für das Token.
     * @return Ein signiertes JWT-Token als `String`.
     */
    public String generateToken(UserDetails userDetails) {
        logger.info("🛠️ Generiere JWT-Token für Benutzer: {}", userDetails.getUsername());

        String token = Jwts.builder()
                .setSubject(userDetails.getUsername()) // Benutzername als Subject
                .setIssuedAt(new Date()) // Erstellungszeitpunkt
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // Ablaufzeit
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Signatur mit HMAC SHA-256
                .compact();

        logger.debug("✅ JWT-Token erfolgreich generiert: {}", token);
        return token;
    }

    /**
     * Überprüft, ob das übergebene JWT-Token gültig ist.
     *
     * @param token Das zu überprüfende JWT-Token.
     * @return `true`, wenn das Token gültig ist, sonst `false`.
     */
    public boolean validateToken(String token) {
        try {
            logger.info("🔍 Validierung des JWT-Tokens...");
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            logger.info("✅ JWT-Token ist gültig.");
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("⚠️ JWT-Token ist abgelaufen: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.warn("⚠️ Nicht unterstütztes JWT-Token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.warn("⚠️ Ungültiges JWT-Token: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.warn("⚠️ Ungültige JWT-Signatur: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("⚠️ Leeres oder ungültiges JWT-Token: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extrahiert den Benutzernamen aus dem übergebenen JWT-Token.
     *
     * @param token Das JWT-Token.
     * @return Der Benutzername (`subject`) aus dem Token.
     */
    public String getUsernameFromToken(String token) {
        logger.info("📜 Extrahiere Benutzername aus JWT-Token...");
        String username = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        logger.info("✅ Benutzername aus Token: {}", username);
        return username;
    }
}
