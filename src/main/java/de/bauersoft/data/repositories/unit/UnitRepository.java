package de.bauersoft.data.repositories.unit;

import de.bauersoft.data.entities.unit.Unit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface UnitRepository extends JpaRepository<Unit, Long>, JpaSpecificationExecutor<Unit>
{
    boolean existsUnitByName(String name);

    List<Unit> findAllByName(Pageable pageable, String name);

    int countAllByName(String name);

    Set<Unit> findAllByShorthand(String shorthand);

    Set<Unit> findAllByParentUnitName(String parentUnitName);

    Set<Unit> findAllByParentFactor(float parentFactor);
}
