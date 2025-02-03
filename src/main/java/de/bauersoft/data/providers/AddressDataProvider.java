package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.data.entities.address.Address;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.services.AddressService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class AddressDataProvider implements ConfigurableFilterDataProvider<Address, Void, List<SerializableFilter<Address,?>>> {

	private List<SerializableFilter<Address,?>> filter;
	private AddressService service;
	private List<DataProviderListener<Address> > listeners = new ArrayList<DataProviderListener<Address>>();
	public AddressDataProvider(AddressService service) {
		this.service = service;
	}
	
	@Override
	public boolean isInMemory() {
		return false;
	}
	
	@Override
	public Object getId(Address item) {
		return item.getId();
	}
	
	@Override
	public int size(Query<Address, Void> query) {
		return (int) this.service.count(filter);
	}

	@Override
	public Stream<Address> fetch(Query<Address, Void> query) {
		return this.service.fetchAll(filter,query.getSortOrders()).stream().skip(query.getOffset()).limit(query.getLimit());
	}
	
	@Override
	public void refreshItem(Address item) {
		// TODO Auto-generated method stub
	}

	@Override
	public void refreshAll() {
		DataChangeEvent<Address> dataChangeEvent = new DataChangeEvent<Address>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}

	@Override
	public Registration addDataProviderListener(DataProviderListener<Address> listener) {
		return Registration.addAndRemove(listeners, listener);
	}

	@Override
	public void setFilter(List<SerializableFilter<Address,?>> filter) {
		this.filter = filter;
		DataChangeEvent<Address> dataChangeEvent = new DataChangeEvent<Address>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}
}
