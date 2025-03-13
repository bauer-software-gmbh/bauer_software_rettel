package de.bauersoft.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import de.bauersoft.data.entities.role.Role;
import de.bauersoft.mobile.security.JwtAuthenticationFilter;
import de.bauersoft.views.login.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    //private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(/*JwtAuthenticationFilter jwtAuthenticationFilter*/) {
       // this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // API SECURITY: JWT für `/api/**`
//    @Bean
//    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .securityMatcher(new AntPathRequestMatcher("/api/**"))
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/**").permitAll()  // Auth-Endpoints offen
//                        .anyRequest().authenticated()  // Rest braucht Auth
//                )
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(new AntPathRequestMatcher("/api/**")) // WICHTIG: Nur "/api/**" absichern
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll() // 🚀 ALLE API-Requests offen
                );

        return http.build();
    }

    // UI SECURITY: Vaadin für `/ui/**`
//    @Bean
//    public SecurityFilterChain uiSecurityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .securityMatcher(new AntPathRequestMatcher("/ui/**"))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/ui/login", "/ui/public/**", "/icons/**","/images/**").permitAll()
//                        .anyRequest().authenticated()  // 🔐 Rest erfordert Login
//                )
//                .csrf(csrf -> csrf.ignoringRequestMatchers("/ui/**"));
//
//        // ✅ Vaadin Login-View setzen
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain uiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(new AntPathRequestMatcher("/ui/**")) // Nur für `/ui/**`
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // 🚨 **ALLES erlauben**
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/images/**");
    }

}