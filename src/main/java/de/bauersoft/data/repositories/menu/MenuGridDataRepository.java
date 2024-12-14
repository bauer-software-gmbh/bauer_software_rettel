package de.bauersoft.data.repositories.menu;

import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class MenuGridDataRepository extends AbstractGridDataRepository<Menu>
{
    public MenuGridDataRepository()
    {
        super(Menu.class);
    }
}
