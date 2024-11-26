package de.bauersoft.data.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.Registration;

import de.bauersoft.data.entities.Course;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.services.CourseService;

@Service
public class CourseDataProvider  implements ConfigurableFilterDataProvider<Course, Void, List<SerializableFilter<Course,?>>> {

	private List<SerializableFilter<Course,?>> filter;
	private CourseService service;
	private List<DataProviderListener<Course> > listeners = new ArrayList<DataProviderListener<Course>>();
	public CourseDataProvider(CourseService service) {
		this.service = service;
	}
	
	@Override
	public boolean isInMemory() {
		return false;
	}
	
	@Override
	public Object getId(Course item) {
		return item.getId();
	}
	
	@Override
	public int size(Query<Course, Void> query) {
		return this.service.count(filter);
	}

	@Override
	public Stream<Course> fetch(Query<Course, Void> query) {
		return this.service.fetchAll(filter,query.getSortOrders()).stream().skip(query.getOffset()).limit(query.getLimit());
	}
	
	@Override
	public void refreshItem(Course item) {
		// TODO Auto-generated method stub
	}

	@Override
	public void refreshAll() {
		DataChangeEvent<Course> dataChangeEvent = new DataChangeEvent<Course>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}

	@Override
	public Registration addDataProviderListener(DataProviderListener<Course> listener) {
		return Registration.addAndRemove(listeners, listener);
	}

	@Override
	public void setFilter(List<SerializableFilter<Course,?>> filter) {
		this.filter = filter;
		DataChangeEvent<Course> dataChangeEvent = new DataChangeEvent<Course>(this);
		this.listeners.forEach(listener->listener.onDataChange(dataChangeEvent));
	}
}
