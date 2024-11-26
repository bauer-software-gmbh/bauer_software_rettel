package de.bauersoft.data.repositories.pattern;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.bauersoft.data.entities.Pattern;

public interface PatternRepository extends JpaRepository<Pattern, Long>,JpaSpecificationExecutor<Pattern>{

}
