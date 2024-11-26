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

import de.bauersoft.data.entities.Component;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.services.ComponentService;

@Service
public class ComponentDataProvider implements ConfigurableFilterDataProvider<Component, Void, List<SerializableFilter<Component,?>>> {

	private List<SerializableFilter<Component,?>> filter;
	private ComponentService service;
	private List<DataProviderListener<Component> > listeners = new ArrayList<DataProviderListener<Component>>();
	public ComponentDataProvider(ComponentService service) {
		this.service = service;
	}
	
	@Override
	public boolean isInMemory() {
		return false;
	}

	@Override
	public int size(Query<Component, Void> query) {
		return this.service.count(filter);
	}

	@Override
	public Stream<Component> fetch(Query<Component, Void> query) {
		return this.service.fetchAll(filter,query.getSortOrders()).stream().skip(query.getOffset()).limit(query.getLimit());
	}

	@Override
	public void refreshItem(Component item) {
		// TODO Auto-generated method stub
	}

	@Override
	public void refreshAll() {
		DataChangeEvent<Component> dataChangeEvent = new DataChangeEvent<Component>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}

	@Override
	public Registration addDataProviderListener(DataProviderListener<Component> listener) {
		return Registration.addAndRemove(listeners, listener);
	}

	@Override
	public void setFilter(List<SerializableFilter<Component,?>> filter) {
		this.filter = filter;
		DataChangeEvent<Component> dataChangeEvent = new DataChangeEvent<Component>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}
}
