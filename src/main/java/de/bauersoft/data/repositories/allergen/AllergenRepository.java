package de.bauersoft.data.repositories.allergen;

import de.bauersoft.data.entities.allergen.Allergen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AllergenRepository extends JpaRepository<Allergen, Long>, JpaSpecificationExecutor<Allergen>
{
}
