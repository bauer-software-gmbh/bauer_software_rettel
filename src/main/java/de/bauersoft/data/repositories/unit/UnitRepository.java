package de.bauersoft.data.repositories.unit;

import de.bauersoft.data.entities.unit.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UnitRepository extends JpaRepository<Unit, Long>, JpaSpecificationExecutor<Unit>
{
    boolean existsUntitByName(String name);
}
