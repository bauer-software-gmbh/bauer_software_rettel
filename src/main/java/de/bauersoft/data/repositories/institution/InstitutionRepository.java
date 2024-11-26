package de.bauersoft.data.repositories.institution;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.bauersoft.data.entities.Institution;

public interface InstitutionRepository extends JpaRepository<Institution, Long>,JpaSpecificationExecutor<Institution>{
}
