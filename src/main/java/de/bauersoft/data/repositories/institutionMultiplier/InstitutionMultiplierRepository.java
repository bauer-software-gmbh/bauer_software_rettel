package de.bauersoft.data.repositories.institutionMultiplier;

import de.bauersoft.data.entities.institutionFieldMultiplier.InstitutionMultiplier;
import de.bauersoft.data.entities.institutionFieldMultiplier.InstitutionMultiplierKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InstitutionMultiplierRepository extends JpaRepository<InstitutionMultiplier, InstitutionMultiplierKey>, JpaSpecificationExecutor<InstitutionMultiplier>
{
//    List<InstitutionMultiplier> findAllByInstitutionId(Long institutionId);

//    @Query("""
//            SELECT im.isLocal
//            FROM InstitutionMultiplier im
//            WHERE im.id.institutionId = :institutionId
//            AND im.id.fieldId = :fieldId
//            AND im.id.courseId = :courseId
//    """)
//    boolean isLocal(Long institutionId, Long fieldId, Long courseId);
//
//    @Transactional
//    @Modifying
//    @Query("""
//            DELETE FROM InstitutionMultiplier im
//            WHERE im.id.institutionId = :institutionId
//            AND im.id.fieldId = :fieldId
//    """)
//    void deleteByInstitutionIdAndFieldId(Long institutionId, Long fieldId);
//
//    @Transactional
//    @Modifying
//    @Query("""
//            DELETE FROM InstitutionMultiplier im
//            WHERE im.id.institutionId = :institutionId
//    """)
//    void deleteAllByInstitutionId(Long institutionId);

}
