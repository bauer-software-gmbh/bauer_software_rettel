package de.bauersoft.mobile.model;

/**
 * Repräsentiert eine Login-Anfrage mit Benutzername und Passwort.
 * <p>
 * Dieser Record wird für die Authentifizierung verwendet und vom Client an den Server gesendet.
 * </p>
 *
 * @param username Der Benutzername des Anwenders.
 * @param password Das Passwort des Anwenders (verschlüsselt oder unverschlüsselt, je nach Implementierung).
 */
public record LoginRequest(String username, String password) {}