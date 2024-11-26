package de.bauersoft.data.repositories.user;

import org.springframework.stereotype.Service;

import de.bauersoft.data.entities.User;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
@Service
public class UserGridDataRepository extends AbstractGridDataRepository<User> {

	public UserGridDataRepository() {
		super(User.class);
		
	}

}
