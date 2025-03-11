package de.bauersoft.mobile.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

//@Configuration
//@EnableWebSecurity
public class ApiSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public ApiSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter){
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
               .securityMatcher(new AntPathRequestMatcher("/api/**")) // 👈 Gilt nur für API-Routen
               .csrf(csrf ->
                        csrf.disable()) // 🔧 Falls JWT genutzt wird
               .authorizeHttpRequests(auth -> auth
                       .requestMatchers("/api/public/**").permitAll() // 🔓 Öffentliche API-Routen
                       .anyRequest().authenticated() // 🔐 API-Requests müssen sich authentifizieren
               );

        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
