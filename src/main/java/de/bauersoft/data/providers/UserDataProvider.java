package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.services.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class UserDataProvider implements ConfigurableFilterDataProvider<User, Void, List<SerializableFilter<User,?>>> {

	private List<SerializableFilter<User,?>> filter;
	private UserService service;
	private List<DataProviderListener<User> > listeners = new ArrayList<DataProviderListener<User>>();
	public UserDataProvider(UserService service) {
		this.service = service;
	}
	
	@Override
	public boolean isInMemory() {
		return false;
	}

	@Override
	public int size(Query<User, Void> query) {
		return (int) this.service.count(filter);
	}

	@Override
	public Stream<User> fetch(Query<User, Void> query) {
		return this.service.fetchAll(filter, query.getSortOrders())
				.stream()
				.sorted(Comparator.comparing(i -> i.getName().toLowerCase())) // Standard-Sortierung nach Name
				.skip(query.getOffset())
				.limit(query.getLimit());
	}

	@Override
	public void refreshItem(User item) {
		// TODO Auto-generated method stub
	}

	@Override
	public void refreshAll() {
		DataChangeEvent<User> dataChangeEvent = new DataChangeEvent<User>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}

	@Override
	public Registration addDataProviderListener(DataProviderListener<User> listener) {
		return Registration.addAndRemove(listeners, listener);
	}

	@Override
	public void setFilter(List<SerializableFilter<User,?>> filter) {
		this.filter = filter;
		DataChangeEvent<User> dataChangeEvent = new DataChangeEvent<User>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}
}
