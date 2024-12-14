package de.bauersoft.data.repositories.component;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.bauersoft.data.entities.Component;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ComponentRepository extends JpaRepository<Component, Long>, JpaSpecificationExecutor<Component>
{
    @Query(value = """
        SELECT c.* 
        FROM component c 
        JOIN menu_pattern_components m ON c.id = m.component_id 
        WHERE m.menu_id = ?1 
          AND ((?2 IS NULL AND m.pattern_id IS NULL) OR (?2 IS NOT NULL AND m.pattern_id = ?2))
    """, nativeQuery = true)
    List<Component> findComponentsByIds(Long menuId, Long patternId);

}
