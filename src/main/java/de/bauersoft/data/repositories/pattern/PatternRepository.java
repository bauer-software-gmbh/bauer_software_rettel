package de.bauersoft.data.repositories.pattern;

import de.bauersoft.data.entities.pattern.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PatternRepository extends JpaRepository<Pattern, Long>, JpaSpecificationExecutor<Pattern>
{
    boolean existsByName(String name);

    Pattern findByName(String name);
}
