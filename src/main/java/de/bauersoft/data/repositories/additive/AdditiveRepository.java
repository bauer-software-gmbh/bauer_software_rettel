package de.bauersoft.data.repositories.additive;

import de.bauersoft.data.entities.additive.Additive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdditiveRepository extends JpaRepository<Additive, Long>, JpaSpecificationExecutor<Additive>
{
}
