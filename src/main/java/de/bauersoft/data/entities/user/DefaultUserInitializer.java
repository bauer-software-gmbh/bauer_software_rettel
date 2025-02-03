package de.bauersoft.data.entities.user;

import de.bauersoft.data.entities.role.Role;
import de.bauersoft.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultUserInitializer
{

    @Bean
    public CommandLineRunner initializeDefaultUsers(UserService userService)
    {
        return args ->
        {
            if(userService.getRepository().existsBySurname("Rettel")) return;
            userService.createUser("Tobial", "Rettel", "rettel@bauer-soft.de", "1234", Role.values());
        };
    }
}
