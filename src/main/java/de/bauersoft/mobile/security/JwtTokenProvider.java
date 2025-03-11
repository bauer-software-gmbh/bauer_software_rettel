package de.bauersoft.mobile.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    // Schlüssel für das Token-Management
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Token generieren
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())  // Benutzername als Subject
                .setIssuedAt(new Date())  // Erstellungszeitpunkt
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))  // Ablaufzeit
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // Signieren mit HMAC SHA-256
                .compact();
    }

    // Token validieren
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("JWT Token ist abgelaufen: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.warn("Nicht unterstütztes JWT Token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.warn("Ungültiges JWT Token: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.warn("Ungültige Signatur: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("Leeres oder ungültiges JWT Token: {}", e.getMessage());
        }
        return false;
    }

    // Benutzername aus Token extrahieren
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}
