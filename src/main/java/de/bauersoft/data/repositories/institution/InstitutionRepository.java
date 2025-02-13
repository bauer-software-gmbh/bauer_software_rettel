package de.bauersoft.data.repositories.institution;

import de.bauersoft.data.entities.institution.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface InstitutionRepository extends JpaRepository<Institution, Long>, JpaSpecificationExecutor<Institution>
{
    boolean existsInstitutionsByAddressId(Long addressId);

    List<Institution> findAllByUsersId(Long userId);
}
