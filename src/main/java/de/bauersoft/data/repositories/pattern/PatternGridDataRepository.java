package de.bauersoft.data.repositories.pattern;

import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class PatternGridDataRepository extends AbstractGridDataRepository<Pattern>
{
    public PatternGridDataRepository()
    {
        super(Pattern.class);
    }
}
