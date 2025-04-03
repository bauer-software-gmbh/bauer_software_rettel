package de.bauersoft.mobile.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility-Klasse zur AES-Verschlüsselung und -Entschlüsselung.
 * <p>
 * Diese Klasse bietet eine Methode zur Entschlüsselung von Base64-codierten Strings,
 * die mit dem AES-Algorithmus verschlüsselt wurden.
 * </p>
 */
@Component
public class AESUtil {

    private static final Logger logger = LoggerFactory.getLogger(AESUtil.class);

    private static String SECRET_KEY;

    /**
     * Setzt den AES-Schlüssel aus den Spring Boot `application.properties`.
     *
     * @param secretKey Der geheime AES-Schlüssel als String.
     */
    @Value("${aes.secret}")
    public void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
        logger.info("🔑 AES-Schlüssel erfolgreich geladen.");
    }

    private static final String ALGORITHM = "AES";

    /**
     * Entschlüsselt einen mit AES verschlüsselten Base64-String.
     *
     * @param encryptedData Der verschlüsselte String im Base64-Format.
     * @return Der entschlüsselte Klartext.
     * @throws Exception Falls ein Fehler bei der Entschlüsselung auftritt.
     */
    public String decrypt(String encryptedData) throws Exception {
        try {
            logger.info("🔓 Entschlüsselungsanfrage erhalten.");

            // Prüfe, ob der AES-Schlüssel gesetzt ist
            if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
                logger.error("❌ AES-Schlüssel nicht gesetzt. Entschlüsselung nicht möglich.");
                throw new IllegalStateException("AES-Schlüssel wurde nicht gesetzt.");
            }

            logger.debug("📩 Empfangene verschlüsselte Daten: {}", encryptedData);

            // Schlüssel in Bytes umwandeln
            byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);

            // Cipher für AES-Entschlüsselung initialisieren
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // Base64-String dekodieren und entschlüsseln
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);

            logger.info("✅ Entschlüsselung erfolgreich.");
            logger.debug("🔓 Entschlüsselter Klartext: {}", decryptedText);

            return decryptedText;

        } catch (Exception e) {
            logger.error("❌ Fehler bei der Entschlüsselung: {}", e.getMessage(), e);
            throw new Exception("Fehler bei der Entschlüsselung: " + e.getMessage());
        }
    }
}
