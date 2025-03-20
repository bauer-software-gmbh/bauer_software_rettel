package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.services.ServiceBase;
import de.bauersoft.services.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class UserDataProvider extends FilterDataProvider<User, Long>
{
	public UserDataProvider(UserService userService)
	{
		super(userService);
	}
}
