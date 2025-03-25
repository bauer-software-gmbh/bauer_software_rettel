package de.bauersoft.data.entities.flesh;

import de.bauersoft.data.repositories.flesh.FleshRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DefaultFleshInitializer
{
    @Bean
    @Primary
    public CommandLineRunner initializeDefaultFlesh(FleshRepository fleshRepository)
    {
        return args ->
        {
            for(DefaultFlesh defaultFlesh : DefaultFlesh.values())
            {
                if(fleshRepository.existsByName(defaultFlesh.getName()))
                    continue;

                Flesh flesh = new Flesh();
                flesh.setName(defaultFlesh.getName());

                fleshRepository.save(flesh);
            }
        };
    }
}
