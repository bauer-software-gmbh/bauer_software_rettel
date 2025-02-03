package de.bauersoft.data.repositories.institutionfields;

import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.data.entities.institution.InstitutionFieldKey;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface InstitutionFieldsRepository extends JpaRepository<InstitutionField, InstitutionFieldKey>, JpaSpecificationExecutor<InstitutionField>
{
    List<InstitutionField> findAllByInstitutionId(Long institutionId);
//    @Query("select i from InstitutionField i where i.id.institutionId = :id")
//    public Set<InstitutionField> findAllByInstitutionId(@Param("id") Long institutionId);
//
//    @Modifying
//    @Transactional
//    @Query("delete from InstitutionField i where i.id.institutionId = :id")
//    public void deleteAllByInstitutionId(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("delete from InstitutionField i where i.id.institutionId = :institutionId")
    void deleteAllByInstitutionId(Long institutionId);

    boolean existsByFieldId(Long id);
}
