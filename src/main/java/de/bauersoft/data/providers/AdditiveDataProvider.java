package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.*;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.services.AdditiveService;
import de.bauersoft.services.ServiceBase;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class AdditiveDataProvider
		extends CallbackDataProvider<Additive, Specification<Additive>>
		implements DataProviderBase<Additive, Long, ServiceBase<Additive, Long>>
{

	private final ConfigurableFilterDataProvider<Additive, Void, Specification<Additive>> configurableFilterDataProvider;
	private final AdditiveService service;

	public AdditiveDataProvider(AdditiveService service)
	{
		super(query ->
		{
			Pageable pageable = PageRequest.of(query.getOffset() / query.getLimit(), query.getLimit());

			Specification<Additive> filter = query.getFilter().orElse(Specification.where(null));

			return service.getRepository().findAll(filter, pageable).stream();

		}, query ->
		{
			Specification<Additive> filter = query.getFilter().orElse(Specification.where(null));

			return (int) service.getRepository().count(filter);
		});

		this.configurableFilterDataProvider = this.withConfigurableFilter();
        this.service = service;
    }

	@Override
	public AdditiveDataProvider setFilter(Specification<Additive> filter)
	{
		this.configurableFilterDataProvider.setFilter(filter);
		return this;
	}

	@Override
	public CallbackDataProvider<Additive, Specification<Additive>> getCallbackDataProvider()
	{
		return this;
	}

	@Override
	public ConfigurableFilterDataProvider<Additive, Void, Specification<Additive>> getConfigurableFilterDataProvider()
	{
		return configurableFilterDataProvider;
	}

	@Override
	public AdditiveService getService()
	{
		return service;
	}
	//	private List<SerializableFilter<Additive,?>> filter;
//	private AdditiveService service;
//	private List<DataProviderListener<Additive> > listeners = new ArrayList<DataProviderListener<Additive>>();
//	public AdditiveDataProvider(AdditiveService service) {
//		this.service = service;
//	}
//
//	@Override
//	public boolean isInMemory() {
//		return false;
//	}
//
//	@Override
//	public Object getId(Additive item) {
//		return item.getId();
//	}
//
//	@Override
//	public int size(Query<Additive, Void> query) {
//		return (int) this.service.count(filter);
//	}
//
//	@Override
//	public Stream<Additive> fetch(Query<Additive, Void> query) {
//		return this.service.fetchAll(filter,query.getSortOrders()).stream().skip(query.getOffset()).limit(query.getLimit());
//	}
//
//	@Override
//	public void refreshItem(Additive item) {
//		// TODO Auto-generated method stub
//	}
//
//	@Override
//	public void refreshAll() {
//		DataChangeEvent<Additive> dataChangeEvent = new DataChangeEvent<Additive>(this);
//		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
//	}
//
//	@Override
//	public Registration addDataProviderListener(DataProviderListener<Additive> listener) {
//		return Registration.addAndRemove(listeners, listener);
//	}
//
//	@Override
//	public void setFilter(List<SerializableFilter<Additive,?>> filter) {
//		this.filter = filter;
//		DataChangeEvent<Additive> dataChangeEvent = new DataChangeEvent<Additive>(this);
//		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
//	}
}
