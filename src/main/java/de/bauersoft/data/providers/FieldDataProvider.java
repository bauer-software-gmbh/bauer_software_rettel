package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.services.FieldService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FieldDataProvider implements ConfigurableFilterDataProvider<Field, Void, List<SerializableFilter<Field,?>>> {

	private List<SerializableFilter<Field,?>> filter;
	private FieldService service;
	private List<DataProviderListener<Field> > listeners = new ArrayList<DataProviderListener<Field>>();
	public FieldDataProvider(FieldService service) {
		this.service = service;
	}
	
	@Override
	public boolean isInMemory() {
		return false;
	}
	
	@Override
	public Object getId(Field item) {
		return item.getId();
	}
	
	@Override
	public int size(Query<Field, Void> query) {
		return (int) this.service.count(filter);
	}

	@Override
	public Stream<Field> fetch(Query<Field, Void> query) {
		return this.service.fetchAll(filter, query.getSortOrders())
				.stream()
				.sorted(Comparator.comparing(i -> i.getName().toLowerCase())) // Standard-Sortierung nach Name
				.skip(query.getOffset())
				.limit(query.getLimit());
	}
	
	@Override
	public void refreshItem(Field item) {
		// TODO Auto-generated method stub
	}

	@Override
	public void refreshAll() {
		DataChangeEvent<Field> dataChangeEvent = new DataChangeEvent<Field>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}

	@Override
	public Registration addDataProviderListener(DataProviderListener<Field> listener) {
		return Registration.addAndRemove(listeners, listener);
	}

	@Override
	public void setFilter(List<SerializableFilter<Field,?>> filter) {
		this.filter = filter;
		DataChangeEvent<Field> dataChangeEvent = new DataChangeEvent<Field>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}
}
