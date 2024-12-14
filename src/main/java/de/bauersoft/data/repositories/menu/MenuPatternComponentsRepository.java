package de.bauersoft.data.repositories.menu;

import de.bauersoft.data.entities.Component;
import de.bauersoft.data.entities.menu.MenuPatternComponents;
import de.bauersoft.data.entities.menu.MenuPatternComponentsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuPatternComponentsRepository extends JpaRepository<MenuPatternComponents, Long>, JpaSpecificationExecutor<MenuPatternComponents>
{
    //@Query("select m from MenuPatternComponents m where m.id.menuId = ?1 and m.id.patternId = ?2")
//    @Query(value = "SELECT c.* FROM component c " +
//            "JOIN menu_pattern_components m ON c.id = m.component_id " +
//            "WHERE m.menu_id = ?1 AND m.pattern_id = ?2",
//            nativeQuery = true)

    //@Query(value = "SELECT * FROM component JOIN menu_pattern_components on menu_pattern_components.component_id WHERE menu_pattern_components.menu_id = ?1 AND menu_pattern_components.pattern_id = ?2", nativeQuery = true)

//    @Query(value = "SELECT m.component_id FROM menu_pattern_components m " +
//            "WHERE m.menu_id = ?1 AND m.pattern_id = ?2",
//            nativeQuery = true)

//    @Query(value = "SELECT c.* FROM component c "
//                    + "JOIN menu_pattern_components m on c.id = m.component_id "
//                    + "WHERE m.menu_id = ?1 AND m.pattern_id IS NULL", nativeQuery = true)

//    @Query(value = """
//    SELECT c.*
//    FROM component c
//    JOIN menu_pattern_components m ON c.id = m.component_id
//    WHERE m.menu_id = ?1
//      AND ((?2 IS NULL AND m.pattern_id IS NULL) OR (?2 IS NOT NULL AND m.pattern_id = ?2))
//""", nativeQuery = true)
//    List<Component> findComponentIdsByIds(Long menuId, Long patternId);
//
//    @Query(value = "SELECT c.* FROM component c "
//            + "JOIN menu_pattern_components m on c.id = m.component_id "
//            + "WHERE m.menu_id = ?1 AND m.pattern_id = ?2 AND c.course_id = ?3", nativeQuery = true)
//    List<Component> findComponentIdsByIds(Long menuId, Long patternId, Long courseId);

    @Query(value = """
        SELECT m.* 
        FROM menu_pattern_components m 
        WHERE m.menu_id = ?1
            AND ((?2 IS NULL AND m.pattern_id IS NULL) OR (?2 IS NOT NULL AND m.pattern_id = ?2))
    """, nativeQuery = true)
    List<MenuPatternComponents> findMenuPatternComponentsByIds(Long menuId, Long patternId);
}
