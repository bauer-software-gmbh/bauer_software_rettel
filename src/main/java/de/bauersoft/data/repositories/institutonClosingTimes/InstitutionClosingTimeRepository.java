package de.bauersoft.data.repositories.institutonClosingTimes;

import de.bauersoft.data.entities.institutionClosingTime.InstitutionClosingTime;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface InstitutionClosingTimeRepository extends JpaRepository<InstitutionClosingTime, Long>, JpaSpecificationExecutor<InstitutionClosingTime>
{
    @Transactional
    @Modifying
    @Query("""
            DELETE FROM InstitutionClosingTime i
            WHERE i.id = :id
    """)
    void deleteById(Long id);
}
