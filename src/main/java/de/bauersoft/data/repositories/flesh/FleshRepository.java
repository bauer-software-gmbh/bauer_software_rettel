package de.bauersoft.data.repositories.flesh;

import de.bauersoft.data.entities.flesh.Flesh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FleshRepository extends JpaRepository<Flesh, Long>, JpaSpecificationExecutor<Flesh>
{
    boolean existsByName(String name);
}
