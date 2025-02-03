package de.bauersoft.data.repositories.component;

import de.bauersoft.data.entities.component.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ComponentRepository extends JpaRepository<Component, Long>, JpaSpecificationExecutor<Component>
{
    boolean existsByCourseId(Long courseId);

    boolean existsByRecipesId(Long recipesId);

    Set<Component> findByCourseId(Long courseId);

    @Query("""
    SELECT c
    FROM Component c
    JOIN c.recipes r
    JOIN r.patterns p
    WHERE p.id = :patternId
    AND c.course.id = :courseId
    GROUP BY c.id
    HAVING COUNT(DISTINCT p.id) = (
        SELECT COUNT(p2)
        FROM Recipe r2
        JOIN r2.patterns p2
        WHERE r2 IN (
            SELECT r3
            FROM Component c3
            JOIN c3.recipes r3
            WHERE c3.id = c.id
        )
        AND p2.id = :patternId
    )
    """)
    List<Component> findComponentsByCourseIdAndPatternId(Long courseId, Long patternId);
}
