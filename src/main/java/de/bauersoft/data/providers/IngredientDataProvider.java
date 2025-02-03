package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.data.entities.ingredient.Ingredient;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.services.IngredientService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class IngredientDataProvider
		implements ConfigurableFilterDataProvider<Ingredient, Void, List<SerializableFilter<Ingredient, ?>>> {

	private List<SerializableFilter<Ingredient, ?>> filter;
	private IngredientService service;
	private List<DataProviderListener<Ingredient>> listeners = new ArrayList<DataProviderListener<Ingredient>>();

	public IngredientDataProvider(IngredientService service) {
		this.service = service;
	}

	@Override
	public boolean isInMemory() {
		return true;
	}
	
	@Override
	public Object getId(Ingredient item) {
		return item.getId();
	}
	
	@Override
	public int size(Query<Ingredient, Void> query) {
		return (int) this.service.count(filter);
	}

	@Override
	public Stream<Ingredient> fetch(Query<Ingredient, Void> query) {
		return this.service.fetchAll(filter,query.getSortOrders()).stream().skip(query.getOffset()).limit(query.getLimit());
	}

	@Override
	public void refreshItem(Ingredient item) {
		// TODO Auto-generated method stub
	}

	@Override
	public void refreshAll() {
		DataChangeEvent<Ingredient> dataChangeEvent = new DataChangeEvent<Ingredient>(this);
		this.listeners.forEach(listener -> listener.onDataChange(dataChangeEvent));
	}

	@Override
	public Registration addDataProviderListener(DataProviderListener<Ingredient> listener) {
		return Registration.addAndRemove(listeners, listener);
	}

	@Override
	public void setFilter(List<SerializableFilter<Ingredient, ?>> filter) {
		this.filter = filter;
		DataChangeEvent<Ingredient> dataChangeEvent = new DataChangeEvent<Ingredient>(this);
		this.listeners.forEach(listener -> listener.onDataChange(dataChangeEvent));
	}
}
