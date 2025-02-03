package de.bauersoft.data.repositories.component;

import de.bauersoft.data.entities.component.Component;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class ComponentGridDataRepository extends AbstractGridDataRepository<Component>
{
        public ComponentGridDataRepository()
        {
            super(Component.class);
        }
}
