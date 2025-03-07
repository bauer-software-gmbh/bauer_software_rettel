package de.bauersoft.data.entities.field;

import de.bauersoft.data.repositories.field.FieldRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultFieldInitializer
{
    @Bean
    public CommandLineRunner initializeDefaultFields(FieldRepository fieldRepository)
    {
        return args ->
        {
            for(DefaultField value : DefaultField.values())
            {
                if(fieldRepository.existsByName(value.getName()))
                    continue;

                Field field = new Field();
                field.setName(value.getName());

                fieldRepository.save(field);
            }
        };
    }
}
