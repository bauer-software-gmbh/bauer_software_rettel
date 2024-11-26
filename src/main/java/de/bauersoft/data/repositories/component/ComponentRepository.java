package de.bauersoft.data.repositories.component;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.bauersoft.data.entities.Component;

public interface ComponentRepository extends JpaRepository<Component, Long>, JpaSpecificationExecutor<Component> {
}
