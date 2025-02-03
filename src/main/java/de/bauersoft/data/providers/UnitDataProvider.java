package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.*;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.data.entities.unit.Unit;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.services.UnitService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class UnitDataProvider implements ConfigurableFilterDataProvider<Unit, Void, List<SerializableFilter<Unit,?>>>, DataProvider<Unit, Void> {

	private List<SerializableFilter<Unit,?>> filter;
	private UnitService service;
	private List<DataProviderListener<Unit> > listeners = new ArrayList<DataProviderListener<Unit>>();
	public UnitDataProvider(UnitService service) {
		this.service = service;
	}
	
	@Override
	public boolean isInMemory() {
		return false;
	}

	@Override
	public int size(Query<Unit, Void> query) {
		return (int) this.service.count(filter);
	}

	@Override
	public Stream<Unit> fetch(Query<Unit, Void> query) {
		return this.service.fetchAll(filter,query.getSortOrders()).stream().skip(query.getOffset()).limit(query.getLimit());
	}

	@Override
	public void refreshItem(Unit item)
	{
		DataChangeEvent<Unit> dataChangeEvent = new DataChangeEvent.DataRefreshEvent<>(this, item);
		this.listeners.forEach(listener -> listener.onDataChange(dataChangeEvent));
	}

	@Override
	public void refreshAll() {
		DataChangeEvent<Unit> dataChangeEvent = new DataChangeEvent<Unit>(this);
		this.listeners.forEach(listener-> listener.onDataChange(dataChangeEvent));
	}

	@Override
	public Registration addDataProviderListener(DataProviderListener<Unit> listener) {
		return Registration.addAndRemove(listeners, listener);
	}

	@Override
	public void setFilter(List<SerializableFilter<Unit,?>> filter) {
		this.filter = filter;
		DataChangeEvent<Unit> dataChangeEvent = new DataChangeEvent<Unit>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}
}
