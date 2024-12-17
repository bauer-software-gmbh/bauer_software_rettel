package de.bauersoft.data.repositories.menuBuilder;

import de.bauersoft.data.entities.menu.Menu;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MBMenuRepository extends JpaRepository<Menu, Long>, JpaSpecificationExecutor<Menu>
{
    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO menu_pattern_components (menu_id, pattern_id, component_id)
            VALUES (?1, ?2, ?3)
            ON DUPLICATE KEY UPDATE menu_id = VALUES(menu_id), pattern_id = VALUES(pattern_id), component_id = VALUES(component_id)
    """, nativeQuery = true)
    void upsertMenuPatternComponent(Long menuId, Long patternId, Long componentId);

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM menu_pattern_components
            WHERE menu_id = ?1
    """, nativeQuery = true)
    void deleteByMenuId(Long menuId);
}
