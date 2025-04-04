package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.services.InstitutionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class InstitutionDataProvider implements ConfigurableFilterDataProvider<Institution, Void, List<SerializableFilter<Institution, ?>>>
{

	private List<SerializableFilter<Institution, ?>> filter;
	private InstitutionService service;
	private List<DataProviderListener<Institution>> listeners = new ArrayList<DataProviderListener<Institution>>();

	public InstitutionDataProvider(InstitutionService service)
	{
		this.service = service;
	}

	@Override
	public boolean isInMemory()
	{
		return true;
	}

	@Override
	public Object getId(Institution item)
	{
		return item.getId();
	}

	@Override
	public int size(Query<Institution, Void> query)
	{
		return (int) this.service.count(filter);
	}

	@Override
	public Stream<Institution> fetch(Query<Institution, Void> query) {
//		return this.service.fetchAll(filter, query.getSortOrders()).stream().skip(query.getOffset()).limit(query.getLimit());

		return this.service.fetchAll(filter, query.getSortOrders())
				.stream()
				.sorted(Comparator.comparing(i -> i.getName().toLowerCase())) // Standard-Sortierung nach Name
				.skip(query.getOffset())
				.limit(query.getLimit());
	}

	@Override
	public void refreshItem(Institution item)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void refreshAll()
	{
		DataChangeEvent<Institution> dataChangeEvent = new DataChangeEvent<Institution>(this);
		this.listeners.forEach(listener -> listener.onDataChange(dataChangeEvent));
	}

	@Override
	public Registration addDataProviderListener(DataProviderListener<Institution> listener)
	{
		return Registration.addAndRemove(listeners, listener);
	}

	@Override
	public void setFilter(List<SerializableFilter<Institution, ?>> filter)
	{
		this.filter = filter;
		DataChangeEvent<Institution> dataChangeEvent = new DataChangeEvent<Institution>(this);
		this.listeners.forEach(listener -> listener.onDataChange(dataChangeEvent));
	}
}