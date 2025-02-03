package de.bauersoft.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.repositories.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class AuthenticatedUser {

    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;

    public AuthenticatedUser(AuthenticationContext authenticationContext, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
    }

    @Transactional
    public Optional<User> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(userDetails -> userRepository.findUserByEmail(userDetails.getUsername()));
    }

    public void logout() {
        authenticationContext.logout();
    }

}
