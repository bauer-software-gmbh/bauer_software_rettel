package de.bauersoft.data.repositories.pattern;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.bauersoft.data.entities.pattern.Pattern;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatternRepository extends JpaRepository<Pattern, Long>,JpaSpecificationExecutor<Pattern>
{

    @Query("SELECT p FROM Pattern p WHERE p.name = :name")
    Pattern findByName(@Param("name") String name);

    @Transactional
    @Modifying
    @Query("UPDATE Pattern p SET p.name = :#{#pattern.name}, p.description = :#{#pattern.description}, p.religious = :#{#pattern.religious} WHERE p.name = :#{#pattern.name}")
    void updatePattern(@Param("pattern") Pattern pattern);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO pattern (name, description, religious) VALUES (:#{#pattern.name}, :#{#pattern.description}, :#{#pattern.religious}) " +
            "ON DUPLICATE KEY UPDATE name = :#{#pattern.name}, description = :#{#pattern.description}, religious = :#{#pattern.religious}",
            nativeQuery = true)
    void insertOrUpdatePattern(@Param("pattern") Pattern pattern);

}
