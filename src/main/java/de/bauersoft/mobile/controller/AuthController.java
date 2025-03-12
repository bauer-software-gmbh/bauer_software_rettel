package de.bauersoft.mobile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.bauersoft.mobile.model.LoginRequest;
import de.bauersoft.mobile.security.JwtTokenProvider;
import de.bauersoft.mobile.utils.AESUtil;
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

@AnonymousAllowed
@RestController()
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final AESUtil aesUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService, AESUtil aesUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.aesUtil = aesUtil;
    }

    @PostMapping("api/mobile/test")
    public ResponseEntity<?> test(@RequestBody String data) {

        return ResponseEntity.ok(data);
    }

    @PostMapping("api/mobile/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> requestData) {
        try {

            String encryptedData = requestData.get("encryptedData");

            String decryptedData = aesUtil.decrypt(encryptedData);

            ObjectMapper objectMapper = new ObjectMapper();
            LoginRequest loginRequest = objectMapper.readValue(decryptedData, LoginRequest.class);

            // Benutzer authentifizieren
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(), loginRequest.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Benutzer aus Datenbank abrufen
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.username());

            // JWT-Token generieren
            String auth_token = jwtTokenProvider.generateToken(userDetails);

            // Account-Info hinzufügen (optional)
            Map<String, Object> accountInfo = new HashMap<>();
            accountInfo.put("user", loginRequest.username());

            // Antwort mit Token + Benutzerinformationen
            Map<String, Object> response = new HashMap<>();
            response.put("auth_token", auth_token);
            response.put("account_info", accountInfo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
