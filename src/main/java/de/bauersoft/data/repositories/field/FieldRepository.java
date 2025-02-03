package de.bauersoft.data.repositories.field;

import de.bauersoft.data.entities.field.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FieldRepository extends JpaRepository<Field, Long>, JpaSpecificationExecutor<Field>
{
    boolean existsByName(String name);
}
