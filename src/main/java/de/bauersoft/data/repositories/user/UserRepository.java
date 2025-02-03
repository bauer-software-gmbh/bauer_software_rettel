package de.bauersoft.data.repositories.user;

import de.bauersoft.data.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>
{
    User findUserByEmail(String email);

    User findUserByName(String name);

    boolean existsByEmail(String email);

    boolean existsBySurname(String surname);
}
