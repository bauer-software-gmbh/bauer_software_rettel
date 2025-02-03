package de.bauersoft.data.repositories.menuBuilder;

import de.bauersoft.data.entities.component.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MBComponentRepository extends JpaRepository<Component, Long>, JpaSpecificationExecutor<Component>
{

    @Query(value = """
        SELECT c.*
        FROM component c
        JOIN menu_pattern_components m ON c.id = m.component_id
        WHERE m.menu_id = ?1
          AND m.pattern_id = ?2
    """, nativeQuery = true)
    List<Component> findMBComponentsByIds(Long menuId, Long patternId);

    @Query(value = """
        SELECT c.*
        FROM component c
        JOIN component_recipes cr ON c.id = cr.component_id
        JOIN recipe r ON cr.recipe_id = r.id
        JOIN recipe_patterns rp ON r.id = rp.recipe_id
        WHERE rp.pattern_id = ?1
        GROUP BY c.id
        HAVING COUNT(DISTINCT rp.pattern_id) = (
            SELECT COUNT(*)
            FROM recipe_patterns
            WHERE recipe_id IN (
                SELECT recipe_id
                FROM component_recipes
                WHERE component_id = c.id
            )
            AND pattern_id = ?1
        )
    """, nativeQuery = true)
    List<Component> findComponentsByPatternId(Long patternId);

    @Query(value = """
        SELECT c.*
        FROM component c
        WHERE c.course_id = ?1
    """, nativeQuery = true)
    List<Component> findComponentsByCourseId(Long courseId);

    @Query(value = """
        SELECT c.*
        FROM component c
        JOIN component_recipes cr ON c.id = cr.component_id
        JOIN recipe r ON cr.recipe_id = r.id
        JOIN recipe_patterns rp ON r.id = rp.recipe_id
        WHERE rp.pattern_id = ?1
        AND c.course_id = ?2  -- Filtern nach der angegebenen course_id
        GROUP BY c.id
        HAVING COUNT(DISTINCT rp.pattern_id) = (
            SELECT COUNT(*)
            FROM recipe_patterns
            WHERE recipe_id IN (
                SELECT recipe_id
                FROM component_recipes
                WHERE component_id = c.id
            )
            AND pattern_id = ?1
        )
    """, nativeQuery = true)
    List<Component> findComponentsByIds(Long patternId, Long courseId);

}
