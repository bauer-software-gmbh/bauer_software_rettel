package de.bauersoft.data.repositories.field;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.bauersoft.data.entities.Field;

public interface FieldRepository extends JpaRepository<Field, Long>, JpaSpecificationExecutor<Field>
{
}
