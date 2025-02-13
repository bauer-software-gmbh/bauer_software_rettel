package de.bauersoft.data.repositories.field;

import de.bauersoft.data.entities.field.FieldMultiplier;
import de.bauersoft.data.entities.field.FieldMultiplierKey;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FieldMultiplierRepository extends JpaRepository<FieldMultiplier, FieldMultiplierKey>, JpaSpecificationExecutor<FieldMultiplier>
{
    List<FieldMultiplier> findAllByFieldId(Long fieldId);

    @Transactional
    @Modifying
    @Query("""
            delete from FieldMultiplier fm
            where fm.id.fieldId = :fieldId
    """)
    void deleteAllByFieldId(Long fieldId);
}
