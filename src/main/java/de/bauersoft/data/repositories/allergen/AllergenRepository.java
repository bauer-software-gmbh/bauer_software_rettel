package de.bauersoft.data.repositories.allergen;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.bauersoft.data.entities.Allergen;

public interface AllergenRepository extends JpaRepository<Allergen, Long>, JpaSpecificationExecutor<Allergen> {

}
