package de.bauersoft.data.repositories.institution;

import de.bauersoft.data.entities.institution.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface InstitutionRepository extends JpaRepository<Institution, Long>, JpaSpecificationExecutor<Institution>
{
    boolean existsInstitutionsByAddressId(Long addressId);

    List<Institution> findAllByUsersId(Long userId);

    Optional<Institution> findById(Long id);
}
