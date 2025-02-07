package de.bauersoft.data.repositories.institution;

import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institution.InstitutionMultiplier;
import de.bauersoft.data.entities.institution.InstitutionMultiplierKey;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InstitutionMultiplierRepository extends JpaRepository<InstitutionMultiplier, InstitutionMultiplierKey>, JpaSpecificationExecutor<InstitutionMultiplier>
{
    List<InstitutionMultiplier> findAllByInstitutionId(Long institutionId);

    @Query("""
            SELECT im.isLocal
            FROM InstitutionMultiplier im
            WHERE im.id.institutionId = :institutionId
            AND im.id.fieldId = :fieldId
            AND im.id.courseId = :courseId
    """)
    boolean isLocal(Long institutionId, Long fieldId, Long courseId);

    @Transactional
    @Modifying
    @Query("""
            DELETE FROM InstitutionMultiplier im
            WHERE im.id.institutionId = :institutionId
            AND im.id.fieldId = :fieldId
    """)
    void deleteByInstitutionIdAndFieldId(Long institutionId, Long fieldId);

    @Transactional
    @Modifying
    void deleteAllByInstitution(Institution institution);

    @Transactional
    @Modifying
    void deleteAllByInstitutionId(Long institutionId);
}
