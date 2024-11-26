package de.bauersoft.data.repositories.component;

import org.springframework.stereotype.Service;

import de.bauersoft.data.entities.Component;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;

@Service
public class ComponentGridDataRepository extends AbstractGridDataRepository<Component> {

	public ComponentGridDataRepository() {
		super(Component.class);
	}

}
