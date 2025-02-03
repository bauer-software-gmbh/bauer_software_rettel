package de.bauersoft.data.repositories.variant;

import de.bauersoft.data.entities.variant.Variant;
import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Var;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VariantRepository extends JpaRepository<Variant, Long>, JpaSpecificationExecutor<Variant>
{
    boolean existsByPatternId(Long id);

    boolean existsByComponentsId(Long id);

    List<Variant> findAllByMenuId(Long id);

    Optional<Variant> findByMenuIdAndPatternId(Long menuId, Long patternId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Variant v WHERE v.menu.id = :menuId")
    void deleteAllByMenuId(Long menuId);
}
