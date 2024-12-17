package de.bauersoft.data.repositories.menuBuilder;

import de.bauersoft.data.entities.pattern.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MBPatternRepository  extends JpaRepository<Pattern, Long>, JpaSpecificationExecutor<Pattern>
{
    @Query(value = """
        SELECT DISTINCT p.*
        FROM pattern p
        JOIN menu_pattern_components m ON p.id = m.pattern_id
        WHERE m.menu_id = ?1
    """, nativeQuery = true)
    List<Pattern> findMBPatternsByMenuId(Long menuId);
}
