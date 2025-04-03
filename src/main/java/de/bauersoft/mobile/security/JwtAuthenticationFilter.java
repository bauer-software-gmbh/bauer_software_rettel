package de.bauersoft.mobile.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Sicherheitsfilter zur JWT-Authentifizierung.
 * <p>
 * Dieser Filter wird vor jeder API-Anfrage ausgeführt und überprüft,
 * ob ein gültiges JWT-Token vorhanden ist. Falls das Token gültig ist,
 * wird die Authentifizierung in den `SecurityContextHolder` gesetzt.
 * </p>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    /**
     * Konstruktor für `JwtAuthenticationFilter`.
     *
     * @param jwtTokenProvider  Der JWT-Token-Provider für Token-Validierung.
     * @param userDetailsService Der `UserDetailsService`, um Benutzerinformationen zu laden.
     */
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Diese Methode wird bei jeder eingehenden HTTP-Anfrage ausgeführt.
     * <p>
     * Sie überprüft:
     * - Ob ein gültiges JWT-Token in der Anfrage enthalten ist.
     * - Falls ja, wird der Benutzer authentifiziert und dem `SecurityContext` hinzugefügt.
     * </p>
     *
     * @param request     Die eingehende HTTP-Anfrage.
     * @param response    Die HTTP-Antwort.
     * @param filterChain Die Filterkette, die die Anfrage weiterleitet.
     * @throws ServletException Falls ein Servlet-Fehler auftritt.
     * @throws IOException      Falls ein I/O-Fehler auftritt.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        logger.debug("🔍 Prüfe Anfrage auf JWT-Token...");

        String token = getJwtFromRequest(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            logger.info("✅ Gültiges JWT-Token gefunden.");

            String username = jwtTokenProvider.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (userDetails != null) {
                logger.info("🔑 Benutzer authentifiziert: {}", username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                logger.warn("⚠️ JWT-Token ist gültig, aber kein Benutzer gefunden: {}", username);
            }
        } else {
            logger.warn("❌ Kein gültiges JWT-Token gefunden.");
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrahiert das JWT-Token aus dem `Authorization`-Header der Anfrage.
     *
     * @param request Die eingehende HTTP-Anfrage.
     * @return Das JWT-Token als String oder `null`, falls kein gültiges Token vorhanden ist.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (!StringUtils.hasText(bearerToken)) {
            logger.warn("⚠️ Kein Authorization-Header in der Anfrage vorhanden.");
            return null;
        }

        if (!bearerToken.startsWith("Bearer ")) {
            logger.warn("⚠️ Fehlformatierter Authorization-Header: {}", bearerToken);
            return null;
        }

        String token = bearerToken.substring(7);
        logger.debug("📩 Extrahiertes JWT-Token: {}", token);
        return token;
    }
}
