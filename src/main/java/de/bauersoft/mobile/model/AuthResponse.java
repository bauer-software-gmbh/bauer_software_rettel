package de.bauersoft.mobile.model;

/**
 * Repräsentiert die Antwort eines erfolgreichen Authentifizierungsprozesses.
 * <p>
 * Diese Klasse enthält das vom Server generierte JWT-Token und den Benutzernamen.
 * Sie wird nach einer erfolgreichen Anmeldung an den Client zurückgegeben.
 * </p>
 *
 * @param token    Das JWT-Token zur Authentifizierung zukünftiger Anfragen.
 * @param username Der Benutzername des authentifizierten Anwenders.
 */
public record AuthResponse(String token, String username) {}