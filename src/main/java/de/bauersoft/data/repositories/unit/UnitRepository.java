package de.bauersoft.data.repositories.unit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.bauersoft.data.entities.Unit;


public interface UnitRepository extends JpaRepository<Unit, Long>, JpaSpecificationExecutor<Unit>{

}
