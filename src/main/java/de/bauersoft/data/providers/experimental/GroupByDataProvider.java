package de.bauersoft.data.providers.experimental;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;

import com.vaadin.flow.data.provider.hierarchy.HierarchicalConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.shared.Registration;

import de.bauersoft.data.entities.AbstractGroupByEntity;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;

public abstract class GroupByDataProvider<T extends AbstractGroupByEntity<T>> implements HierarchicalConfigurableFilterDataProvider<T, Void, List<SerializableFilter<T, ?>>>, HasGroupBy {
	private List<SerializableFilter<T, ?>> filter;
	private List<DataProviderListener<T>> listeners = new ArrayList<DataProviderListener<T>>();
	private List<String> groupeByList = new ArrayList<String>();
	private final AbstractGridDataRepository<T> repository;
	private final Class<T> beanType;
	
	public GroupByDataProvider(AbstractGridDataRepository<T> repository,Class<T> beanType) {
		this.repository = repository;
		this.beanType = beanType;
	}

	@Override
	public void setFilter(List<SerializableFilter<T, ?>> filter) {
		this.filter = filter;
		DataChangeEvent<T> dataChangeEvent = new DataChangeEvent<T>(this);
		this.listeners.forEach(listener -> listener.onDataChange(dataChangeEvent));
	}

	@Override
	public boolean isInMemory() {
		return false;
	}

	@Override
	public int size(Query<T, Void> query) {
		return 0;
	}

	@Override
	public Stream<T> fetch(Query<T, Void> query) {
		return null;
	}

	@Override
	public void refreshItem(T item) {
		// TODO Auto-generated method stub
	}

	@Override
	public void refreshAll() {
		// TODO Auto-generated method stub
	}

	@Override
	public Registration addDataProviderListener(DataProviderListener<T> listener) {
		return Registration.addAndRemove(listeners, listener);
	}

	@Override
	public int getChildCount(HierarchicalQuery<T, Void> query) {
		if (query.getParent() == null) {
			return !groupeByList.isEmpty()
					? (int) repository.count(filter, new ArrayList<String>(), new ArrayList<Object>(),
							groupeByList.get(0))
					: (int) repository.count(filter);
		} else {
			T parent = query.getParent();
			
			return !groupeByList.isEmpty()
					? ( parent.getParentKeys().size() < groupeByList.size() ?
							(int) repository.count(filter, parent.getParentKeys(), parent.getParentValues(),
							groupeByList.get(groupeByList.indexOf(parent.getKey()) + 1))
							: 
							(int) (int) repository.count(filter, parent.getParentKeys(), parent.getParentValues())
								)
					: (int) repository.count(filter);
		}
	}

	@Override
	public Stream<T> fetchChildren(HierarchicalQuery<T, Void> query) {
		if (query.getParent() == null) {
			return !groupeByList.isEmpty() ? repository.fetchAll(filter, filterSortOrder(query.getSortOrders(),groupeByList) , new ArrayList<String>(),
					new ArrayList<Object>(), groupeByList.get(0)).stream().map(value -> {
						
						T result = null;
						try {
							result = beanType.getConstructor().newInstance();
							result.setValue(value);
							result.setKey(groupeByList.get(0));
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException | NoSuchMethodException | SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}return result;
					}).skip(query.getOffset()).limit(query.getLimit())
					: repository.fetchAll(filter, filterSortOrder(query.getSortOrders(),groupeByList)).stream().skip(query.getOffset())
							.limit(query.getLimit());
		} else {
			T parent = query.getParent();
			return !groupeByList.isEmpty() ? 
					( parent.getParentKeys().size() < groupeByList.size() ?
					repository.fetchAll(filter, filterSortOrder(query.getSortOrders(),groupeByList), parent.getParentKeys(),
					parent.getParentValues(), groupeByList.get(groupeByList.indexOf(parent.getKey()) + 1)).stream()
					.map(value -> {
						T result = null;
						try {
							result = beanType.getConstructor().newInstance();
							result.setParentKeys(parent.getParentKeys());
							result.setParentValues(parent.getParentValues());
							result.setValue(value);
							result.setKey(groupeByList.get(groupeByList.indexOf(parent.getKey()) + 1));
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException | NoSuchMethodException | SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return result;
					}).skip(query.getOffset()).limit(query.getLimit()) : 
						repository.fetchAll(filter, filterSortOrder(query.getSortOrders(),groupeByList), parent.getParentKeys(),
								parent.getParentValues()).stream().skip(query.getOffset()).limit(query.getLimit())
							)
					: repository.fetchAll(filter, filterSortOrder(query.getSortOrders(),groupeByList)).stream().skip(query.getOffset())
							.limit(query.getLimit());
		}
	}
	
	private List<QuerySortOrder> filterSortOrder(List<QuerySortOrder> input,List<String> grouped){
		List<QuerySortOrder> result = new ArrayList<QuerySortOrder>();
		input.stream().filter(item->"generated-group-by".equals(item.getSorted())).findFirst().ifPresent(
		item ->
		result.addAll(grouped.stream().map(
				key-> new QuerySortOrder(key, item.getDirection())).toList()));
		
		result.addAll(input.stream().filter(item->!"generated-group-by".equals(item.getSorted())).toList());
		return result;
	}
	
	@Override
	public boolean hasChildren(T item) {
		return item.getKey() != null && groupeByList.indexOf(item.getKey()) <= groupeByList.size();
	}

	public void setGroupeBy(List<String> groupeByList) {
		this.groupeByList = groupeByList == null ? new ArrayList<String>() : groupeByList;
		DataChangeEvent<T> dataChangeEvent = new DataChangeEvent<T>(this);
		this.listeners.forEach(listener -> listener.onDataChange(dataChangeEvent));
	}
}
