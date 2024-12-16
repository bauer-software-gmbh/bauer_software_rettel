package de.bauersoft.data.repositories.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.bauersoft.data.entities.Menu;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu,Long>,JpaSpecificationExecutor<Menu>{

}
