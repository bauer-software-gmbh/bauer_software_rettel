package de.bauersoft.mobile.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Sicherheitskonfiguration für die API-Endpunkte.
 * <p>
 * Diese Klasse konfiguriert die Sicherheitsrichtlinien für alle API-Requests.
 * - `/api/mobile/login` und `/api/mobile/test` sind **ohne Authentifizierung** erreichbar.
 * - Alle anderen `/api/mobile/**` Endpunkte **erfordern eine Authentifizierung** mit JWT.
 * - CSRF ist **deaktiviert** (da wir stateless JWT verwenden).
 * - Session-Management ist **stateless**, da JWT verwendet wird.
 * </p>
 */
@Configuration
public class ApiSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApiSecurityConfig.class);

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Konstruktor für `ApiSecurityConfig`.
     *
     * @param jwtAuthenticationFilter Der JWT-Filter zur Authentifizierung von Requests.
     */
    public ApiSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Konfiguriert die Sicherheitsrichtlinien für API-Requests.
     *
     * @param http Die `HttpSecurity`-Konfiguration.
     * @return Ein `SecurityFilterChain`-Objekt mit den definierten Sicherheitsrichtlinien.
     * @throws Exception Falls eine Konfigurationsfehler auftritt.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        logger.info("🔐 Konfiguriere Sicherheitsfilter für API-Routen...");

        http
                .securityMatcher("/api/**") // Nur API-Endpunkte betreffen
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/mobile/login", "/api/mobile/test").permitAll() // Öffentlich zugänglich
                        .requestMatchers("/api/mobile/**").authenticated() // Alle anderen erfordern Authentifizierung
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT → Kein Session-Management
                .csrf(AbstractHttpConfigurer::disable) // CSRF deaktivieren, da wir kein session-basiertes Auth verwenden
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS-Standardkonfiguration
                .formLogin(AbstractHttpConfigurer::disable) // Form-basiertes Login deaktivieren
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT-Filter vor Username/Passwort-Authentifizierung setzen

        logger.info("✅ Sicherheitsfilter erfolgreich konfiguriert.");
        return http.build();
    }

    /**
     * Erstellt einen `AuthenticationManager`, um Benutzeranmeldungen zu verwalten.
     *
     * @param authenticationConfiguration Die Spring-Sicherheitskonfiguration.
     * @return Der `AuthenticationManager`.
     * @throws Exception Falls die Konfiguration fehlschlägt.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        logger.info("🛠️ Initialisiere AuthenticationManager...");
        return authenticationConfiguration.getAuthenticationManager();
    }

    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowPrivateNetwork(true);
        config.setAllowedOrigins(List.of("https://localhost","http://localhost:8100"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Allowed HTTP methods
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Apply CORS configuration to all endpoints
        return source;
    }
}
