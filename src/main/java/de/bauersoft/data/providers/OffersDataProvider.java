package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.data.entities.Day;
import de.bauersoft.data.entities.Recipe;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.services.DayService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class OffersDataProvider implements DataProvider<Day, Void> {

    private List<SerializableFilter<Day,?>> filter;
    private List<DataProviderListener<Day>> listeners = new ArrayList<DataProviderListener<Day>>();
    private DayService service;
    public OffersDataProvider(DayService service) {this.service = service;}


    @Override
    public boolean isInMemory() {
        return false;
    }

    // gegen datenbank prüfen
    @Override
    public int size(Query<Day, Void> query) {
        return this.service.count(filter);
    }

    // gegen datenbank prüfen
    // query.XXX müssen einmal aufgerufen werden
    @Override
    public Stream<Day> fetch(Query<Day, Void> query) {
        return this.service.fetchAll(filter, query.getSortOrders()).stream().skip(query.getOffset()).limit(query.getLimit());
    }

    // nicht nötig
    @Override
    public void refreshItem(Day item) {

    }

    @Override
    public void refreshAll() {
        DataChangeEvent<Day> event = new DataChangeEvent<>(this);
        listeners.forEach(listener-> listener.onDataChange(event));
    }


    @Override
    public Registration addDataProviderListener(DataProviderListener<Day> listener) {
        return Registration.addAndRemove(listeners, listener);
    }
}
