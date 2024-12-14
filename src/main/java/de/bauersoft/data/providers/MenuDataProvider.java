package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.services.MenuService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class MenuDataProvider implements ConfigurableFilterDataProvider<Menu, Void, List<SerializableFilter<Menu, ?>>>
{
    private List<SerializableFilter<Menu, ?>> filter;
    private MenuService service;
    private List<DataProviderListener<Menu>> listeners = new ArrayList<>();

    public MenuDataProvider(MenuService service)
    {
        Objects.requireNonNull(service, "MenuService cannot be null.");

        this.service = service;
    }

    @Override
    public void setFilter(List<SerializableFilter<Menu, ?>> filter)
    {
        this.filter = filter;

        DataChangeEvent<Menu> dataChangeEvent = new DataChangeEvent<>(this);
        this.listeners.forEach(listener -> listener.onDataChange(dataChangeEvent));
    }

    @Override
    public boolean isInMemory()
    {
        return false;
    }

    @Override
    public int size(Query<Menu, Void> query)
    {
        return this.service.count(filter);
    }

    @Override
    public Stream<Menu> fetch(Query<Menu, Void> query)
    {
        return this.service.fetchAll(filter, query.getSortOrders()).stream().skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    public void refreshItem(Menu item)
    {

    }

    @Override
    public void refreshAll()
    {
        DataChangeEvent<Menu> dataChangeEvent = new DataChangeEvent<>(this);
        this.listeners.forEach(listeners -> listeners.onDataChange(dataChangeEvent));
    }

    @Override
    public Registration addDataProviderListener(DataProviderListener<Menu> listener)
    {
        return Registration.addAndRemove(listeners, listener);
    }
}
