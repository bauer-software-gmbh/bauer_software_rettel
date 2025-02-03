package de.bauersoft.data.repositories.field;

import de.bauersoft.data.entities.field.FieldMultiplier;
import de.bauersoft.data.entities.field.FieldMultiplierKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FieldMultiplierRepository extends JpaRepository<FieldMultiplier, FieldMultiplierKey>, JpaSpecificationExecutor<FieldMultiplier>
{
    List<FieldMultiplier> findAllByFieldId(Long fieldId);
}
