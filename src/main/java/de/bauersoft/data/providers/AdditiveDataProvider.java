package de.bauersoft.data.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.Registration;

import de.bauersoft.data.entities.Additive;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.services.AdditiveService;

@Service
public class AdditiveDataProvider implements ConfigurableFilterDataProvider<Additive, Void, List<SerializableFilter<Additive,?>>> {

	private List<SerializableFilter<Additive,?>> filter;
	private AdditiveService service;
	private List<DataProviderListener<Additive> > listeners = new ArrayList<DataProviderListener<Additive>>();
	public AdditiveDataProvider(AdditiveService service) {
		this.service = service;
	}
	
	@Override
	public boolean isInMemory() {
		return false;
	}
	
	@Override
	public Object getId(Additive item) {
		return item.getId();
	}
	
	@Override
	public int size(Query<Additive, Void> query) {
		return this.service.count(filter);
	}

	@Override
	public Stream<Additive> fetch(Query<Additive, Void> query) {
		return this.service.fetchAll(filter,query.getSortOrders()).stream().skip(query.getOffset()).limit(query.getLimit());
	}
	
	@Override
	public void refreshItem(Additive item) {
		// TODO Auto-generated method stub
	}

	@Override
	public void refreshAll() {
		DataChangeEvent<Additive> dataChangeEvent = new DataChangeEvent<Additive>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}

	@Override
	public Registration addDataProviderListener(DataProviderListener<Additive> listener) {
		return Registration.addAndRemove(listeners, listener);
	}

	@Override
	public void setFilter(List<SerializableFilter<Additive,?>> filter) {
		this.filter = filter;
		DataChangeEvent<Additive> dataChangeEvent = new DataChangeEvent<Additive>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}
}
