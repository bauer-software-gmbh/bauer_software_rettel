package de.bauersoft.data.repositories.griddata;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.filters.SerializableFilter;

import java.util.List;

public interface GridDataRepository<T> {

	long count(List<SerializableFilter<T, ?>> filters);
	long count(List<SerializableFilter<T, ?>> filters, List<String> parentKeys, List<Object> parentValues);
	long count(List<SerializableFilter<T, ?>> filters, List<String> parentKeys,List<Object> parentValues, String groupBy);
	
	List<T> fetchAll(List<SerializableFilter<T, ?>> filters,List<QuerySortOrder> sortOrder);
	List<T> fetchAll(List<SerializableFilter<T, ?>> filters,List<QuerySortOrder> sortOrder, List<String> parentKeys,List<Object> parentValues);
	List<Object> fetchAll(List<SerializableFilter<T, ?>> filters,List<QuerySortOrder> sortOrder, List<String> parentKeys,List<Object> parentValues, String groupBy);
	
}
