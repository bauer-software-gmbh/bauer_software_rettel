package de.bauersoft.data.repositories.user;

import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class UserGridDataRepository extends AbstractGridDataRepository<User>
{
    public UserGridDataRepository()
    {
        super(User.class);
    }
}
