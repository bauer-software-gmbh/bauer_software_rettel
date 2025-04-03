package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.services.PatternService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class PatternDataProvider implements ConfigurableFilterDataProvider<Pattern, Void, List<SerializableFilter<Pattern,?>>> {

	private List<SerializableFilter<Pattern,?>> filter;
	private PatternService service;
	private List<DataProviderListener<Pattern> > listeners = new ArrayList<DataProviderListener<Pattern>>();
	public PatternDataProvider(PatternService service) {
		this.service = service;
	}
	
	@Override
	public boolean isInMemory() {
		return false;
	}
	
	@Override
	public Object getId(Pattern item) {
		return item.getId();
	}
	
	@Override
	public int size(Query<Pattern, Void> query) {
		return (int) this.service.count(filter);
	}

	@Override
	public Stream<Pattern> fetch(Query<Pattern, Void> query) {
		return this.service.fetchAll(filter, query.getSortOrders())
				.stream()
				.sorted(Comparator.comparing(i -> i.getName().toLowerCase())) // Standard-Sortierung nach Name
				.skip(query.getOffset())
				.limit(query.getLimit());
	}
	
	@Override
	public void refreshItem(Pattern item) {
		// TODO Auto-generated method stub
	}

	@Override
	public void refreshAll() {
		DataChangeEvent<Pattern> dataChangeEvent = new DataChangeEvent<Pattern>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}

	@Override
	public Registration addDataProviderListener(DataProviderListener<Pattern> listener) {
		return Registration.addAndRemove(listeners, listener);
	}

	@Override
	public void setFilter(List<SerializableFilter<Pattern,?>> filter) {
		this.filter = filter;
		DataChangeEvent<Pattern> dataChangeEvent = new DataChangeEvent<Pattern>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}
}
