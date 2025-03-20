package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.services.AllergenService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class AllergenDataProvider implements ConfigurableFilterDataProvider<Allergen, Void, List<SerializableFilter<Allergen,?>>> {

	private List<SerializableFilter<Allergen,?>> filter;
	private AllergenService service;
	private List<DataProviderListener<Allergen> > listeners = new ArrayList<DataProviderListener<Allergen>>();
	public AllergenDataProvider(AllergenService service) {
		this.service = service;
	}
	
	@Override
	public boolean isInMemory() {
		return false;
	}

	@Override
	public int size(Query<Allergen, Void> query) {
		return (int) this.service.count(filter);
	}

	@Override
	public Stream<Allergen> fetch(Query<Allergen, Void> query) {
		return this.service.fetchAll(filter, query.getSortOrders())
				.stream()
				.sorted(Comparator.comparing(i -> i.getName().toLowerCase())) // Standard-Sortierung nach Name
				.skip(query.getOffset())
				.limit(query.getLimit());
	}

	@Override
	public void refreshItem(Allergen item) {
		// TODO Auto-generated method stub
	}

	@Override
	public void refreshAll() {
		DataChangeEvent<Allergen> dataChangeEvent = new DataChangeEvent<Allergen>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}

	@Override
	public Registration addDataProviderListener(DataProviderListener<Allergen> listener) {
		return Registration.addAndRemove(listeners, listener);
	}

	@Override
	public void setFilter(List<SerializableFilter<Allergen,?>> filter) {
		this.filter = filter;
		DataChangeEvent<Allergen> dataChangeEvent = new DataChangeEvent<Allergen>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}
}
