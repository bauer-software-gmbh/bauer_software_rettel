package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.*;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.data.entities.recipe.Recipe;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.services.RecipeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class RecipeDataProvider implements ConfigurableFilterDataProvider<Recipe, Void, List<SerializableFilter<Recipe,?>>>, DataProvider<Recipe, Void> {

	private List<SerializableFilter<Recipe,?>> filter;
	private RecipeService service;
	private List<DataProviderListener<Recipe> > listeners = new ArrayList<DataProviderListener<Recipe>>();
	public RecipeDataProvider(RecipeService service) {
		this.service = service;
	}
	
	@Override
	public boolean isInMemory() {
		return false;
	}

	@Override
	public int size(Query<Recipe, Void> query) {
		return (int) this.service.count(filter);
	}

	@Override
	public Stream<Recipe> fetch(Query<Recipe, Void> query) {
		return this.service.fetchAll(filter,query.getSortOrders()).stream().skip(query.getOffset()).limit(query.getLimit());
	}

	@Override
	public void refreshItem(Recipe item) {
		// TODO Auto-generated method stub
	}

	@Override
	public void refreshAll() {
		DataChangeEvent<Recipe> dataChangeEvent = new DataChangeEvent<Recipe>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}

	@Override
	public Registration addDataProviderListener(DataProviderListener<Recipe> listener) {
		return Registration.addAndRemove(listeners, listener);
	}

	@Override
	public void setFilter(List<SerializableFilter<Recipe,?>> filter) {
		this.filter = filter;
		DataChangeEvent<Recipe> dataChangeEvent = new DataChangeEvent<Recipe>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}
}
