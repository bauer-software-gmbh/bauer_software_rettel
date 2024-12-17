package de.bauersoft.data.repositories.menu;

import de.bauersoft.data.entities.Component;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.bauersoft.data.entities.menu.Menu;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long>, JpaSpecificationExecutor<Menu>
{

}
