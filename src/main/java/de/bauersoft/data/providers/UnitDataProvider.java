package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import de.bauersoft.data.entities.unit.Unit;
import de.bauersoft.services.ServiceBase;
import de.bauersoft.services.UnitService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UnitDataProvider extends CallbackDataProvider<Unit, Specification<Unit>> implements DataProviderBase<Unit, Long, ServiceBase<Unit, Long>>
{
    private ConfigurableFilterDataProvider<Unit, Void, Specification<Unit>> configurableFilterDataProvider;
    private UnitService service;

    public UnitDataProvider(UnitService service)
    {
        super(query ->
        {
            Pageable pageable = PageRequest.of(query.getOffset() / query.getLimit(), query.getLimit());

            Specification<Unit> filter = query.getFilter().orElse(Specification.where(null));

            return service.getRepository().findAll(filter, pageable).stream();

        }, query ->
        {
            Specification<Unit> filter = query.getFilter().orElse(Specification.where(null));
            return (int) service.getRepository().count(filter);
        });

        configurableFilterDataProvider = this.withConfigurableFilter();

        this.service = service;
    }

    public UnitDataProvider setFilter(Specification<Unit> filter)
    {
        configurableFilterDataProvider.setFilter(filter);
        return this;
    }

    @Override
    public CallbackDataProvider<Unit, Specification<Unit>> getCallbackDataProvider()
    {
        return null;
    }

    public ConfigurableFilterDataProvider<Unit, Void, Specification<Unit>> getConfigurableFilterDataProvider()
    {
        return configurableFilterDataProvider;
    }

    @Override
    public UnitService getService()
    {
        return service;
    }

    //    @Override
//    public boolean isInMemory() {
//        return false;
//    }
//
//    @Override
//    public int size(Query<Unit, Void> query) {
//        return (int) fetch(query).count();
//    }
//
//    @Override
//    public Stream<Unit> fetch(Query<Unit, Void> query) {
//        Stream<Unit> stream = service.fetchAll(filter, query.getSortOrders()).stream();
//        for (SerializablePredicate<Unit> predicate : predicates) {
//            stream = stream.filter(predicate);
//        }
//        return stream.skip(query.getOffset()).limit(query.getLimit());
//    }
//
//    @Override
//    public void refreshItem(Unit item) {
//        DataChangeEvent<Unit> dataChangeEvent = new DataChangeEvent.DataRefreshEvent<>(this, item);
//        this.listeners.forEach(listener -> listener.onDataChange(dataChangeEvent));
//    }
//
//    @Override
//    public void refreshAll() {
//        DataChangeEvent<Unit> dataChangeEvent = new DataChangeEvent<>(this);
//        this.listeners.forEach(listener -> listener.onDataChange(dataChangeEvent));
//    }
//
//    @Override
//    public Registration addDataProviderListener(DataProviderListener<Unit> listener) {
//        return Registration.addAndRemove(listeners, listener);
//    }
//
//    @Override
//    public void setFilter(List<SerializableFilter<Unit,?>> filter) {
//        this.filter = filter;
//        refreshAll();
//    }
//
//    public void addFilter(SerializablePredicate<Unit> filter) {
//        this.predicates.add(filter);
//        refreshAll();
//    }
//
//    public void clearFilters() {
//        this.predicates.clear();
//        refreshAll();
//    }
}