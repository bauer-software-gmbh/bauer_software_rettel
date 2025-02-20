package de.bauersoft.data.repositories.institutionAllergen;

import de.bauersoft.data.entities.institution.InstitutionAllergen;
import de.bauersoft.data.entities.institution.InstitutionAllergenKey;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface InstitutionAllergenRepository extends JpaRepository<InstitutionAllergen, InstitutionAllergenKey>, JpaSpecificationExecutor<InstitutionAllergen>
{
    @Transactional
    @Modifying
    @Query("""
            DELETE FROM InstitutionAllergen ia
            WHERE ia.id.institutionFieldId = :institutionFieldId
    """)
    void deleteAllByInstitutionFieldId(Long institutionFieldId);


    @Transactional
    @Modifying
    @Query("""
            DELETE FROM InstitutionAllergen ia
            WHERE ia.id.institutionFieldId = :institutionFieldId
            AND ia.id.allergenId = :allergenId
            """)
    void deleteById(Long institutionFieldId, Long allergenId);
}
