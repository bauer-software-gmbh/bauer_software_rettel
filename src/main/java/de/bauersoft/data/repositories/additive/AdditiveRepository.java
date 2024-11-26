package de.bauersoft.data.repositories.additive;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.bauersoft.data.entities.Additive;

public interface AdditiveRepository extends JpaRepository<Additive, Long>, JpaSpecificationExecutor<Additive> {
}
