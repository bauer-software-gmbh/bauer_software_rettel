package de.bauersoft.data.repositories.field;

import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.fieldMultiplier.FieldMultiplier;
import de.bauersoft.data.entities.fieldMultiplier.FieldMultiplierKey;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FieldMultiplierRepository extends JpaRepository<FieldMultiplier, FieldMultiplierKey>, JpaSpecificationExecutor<FieldMultiplier>
{
    List<FieldMultiplier> findAllByFieldId(Long fieldId);

    Optional<FieldMultiplier> findByFieldAndCourse(Field field, Course course);

    @Transactional
    @Modifying
    @Query("""
            delete from FieldMultiplier fm
            where fm.id.fieldId = :fieldId
    """)
    void deleteAllByFieldId(Long fieldId);
}
