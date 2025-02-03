package de.bauersoft.data.entities.pattern;

import de.bauersoft.data.repositories.pattern.PatternRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class DefaultPatternInitializer
{
    @Bean
    public CommandLineRunner initializeDefaultPattern(PatternRepository patternRepository)
    {
        return args ->
        {
            for(DefaultPattern defaultPattern : DefaultPattern.values())
            {
                if(patternRepository.existsByName(defaultPattern.getName()))
                    continue;

                Pattern pattern = Pattern.builder()
                        .name(defaultPattern.getName())
                        .description(defaultPattern.getDescription())
                        .religious(defaultPattern.getReligious())
                        .build();

                patternRepository.save(pattern);
            }
        };
    }
}
