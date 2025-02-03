package de.bauersoft.data.entities.flesh;

import de.bauersoft.data.repositories.flesh.FleshRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultFleshInitializer
{
    @Bean
    public CommandLineRunner initializeDefaultFlesh(FleshRepository fleshRepository)
    {
        return args ->
        {
            for(DefaultFlesh defaultFlesh : DefaultFlesh.values())
            {
                if(fleshRepository.existsByName(defaultFlesh.getName()))
                    continue;

                Flesh flesh = Flesh.builder()
                        .name(defaultFlesh.getName())
                        .build();

                fleshRepository.save(flesh);
            }
        };
    }
}
