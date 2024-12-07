package de.bauersoft.data.entities.pattern;

import de.bauersoft.data.repositories.pattern.PatternRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class is used to create the DefaultPatterns to ensure the consistency of their existence.
 */
@Configuration
public class DefaultPatternInitializer
{

    @Bean
    public CommandLineRunner initializePatterns(PatternRepository patternRepository)
    {
        return args ->
        {
            for (DefaultPattern defPattern : DefaultPattern.values()) {
                Pattern pattern = new Pattern();
                pattern.setName(defPattern.getName());
                pattern.setDescription(defPattern.getDescription());
                pattern.setReligious(defPattern.getReligious());

                patternRepository.insertOrUpdatePattern(pattern);
            }
        };
    }
}
