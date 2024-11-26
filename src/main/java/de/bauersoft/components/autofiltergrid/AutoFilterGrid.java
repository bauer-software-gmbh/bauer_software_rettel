package de.bauersoft.components.autofiltergrid;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;

import de.bauersoft.components.filters.GridFilter;
import de.bauersoft.components.filters.GridNumberFilter;
import de.bauersoft.components.filters.GridTextFilter;
import de.bauersoft.data.entities.User;
import de.bauersoft.data.filters.SerializableFilter;

public class AutoFilterGrid<T> extends Grid<T> {
	private Map<Column<?>, GridFilter<T, ?>> filters = new HashMap<Grid.Column<?>, GridFilter<T, ?>>();
	private Map<Class<?>, Class<?>> typeToFilter = new HashMap<Class<?>, Class<?>>();
	private Map<Column<?>, Class<?>> columnToFilter = new HashMap<Column<?>, Class<?>>();
	private HeaderRow filterRow;
	private boolean buildFilters;

	public AutoFilterGrid() {
		super();
		buildFilters = false;
	}

	public AutoFilterGrid(Class<T> beanType) {
		this(beanType, true, true);
	}

	public AutoFilterGrid(Class<T> beanType, boolean autoColumnHeaders) {
		this(beanType, autoColumnHeaders, autoColumnHeaders);
	}

	public AutoFilterGrid(Class<T> beanType, boolean autoColumnHeaders, boolean autoBuildFilters) {
		super(beanType, autoColumnHeaders);
		setDefaultFilters();
		buildFilters = autoBuildFilters;
	}

	public AutoFilterGrid(ConfigurableFilterDataProvider<T, ?, List<SerializableFilter<User, ?>>> dataProvider) {
		super();
		setDataProvider(dataProvider);
		setDefaultFilters();
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		if (buildFilters) {
			if (filterRow == null) {
				filterRow = appendHeaderRow();
			}
			buildFilters(getColumns());
		}
	}

	public Column<T> addColumn(String key, Class<? extends GridFilter<T, ?>> filterClass) {
		Column<T> column = super.addColumn(key);
		columnToFilter.put(column, filterClass);
		return column;
	}

	public Column<T> addColumn(ValueProvider<T, ?> valueProvider, Class<? extends GridFilter<T, ?>> filterClass) {
		Column<T> column = super.addColumn(valueProvider);
		columnToFilter.put(column, filterClass);
		return column;
	}

	public Column<T> addColumn(Renderer<T> renderer, Class<? extends GridFilter<T, ?>> filterClass) {
		Column<T> column = super.addColumn(renderer);
		columnToFilter.put(column, filterClass);
		return column;
	}

	public Column<T> addColumn(Renderer<T> renderer, Class<? extends GridFilter<T, ?>> filterClass,
			String... sortingPropertys) {
		Column<T> column = super.addColumn(renderer).setSortProperty(sortingPropertys);
		columnToFilter.put(column, filterClass);
		return column;
	}

	public Column<T> addComponentColumn(ValueProvider<T, ? extends Component> valueProvider,
			Class<? extends GridFilter<T, ?>> filterClass) {
		Column<T> column = super.addComponentColumn(valueProvider);
		columnToFilter.put(column, filterClass);
		return column;
	}

	@SuppressWarnings("unchecked")
	private void buildFilters(List<Column<T>> columnList) {
		getPropertySet().getProperties().filter(item -> !item.isSubProperty()).forEach(item -> {
			if (this.getColumnByKey(item.getName()) != null) {
				Class<? extends GridFilter<T, ?>> filterClass = null;
				try {
					if (columnToFilter.get(this.getColumnByKey(item.getName())) != null) {
						filterClass = (Class<? extends GridFilter<T, ?>>) columnToFilter.get(this.getColumnByKey(item.getName()));
					} else {
						filterClass = (Class<? extends GridFilter<T, ?>>) typeToFilter.get(item.getType());
					}
					if (filterClass != null) {
						GridFilter<T, ?> filter = filterClass.getConstructor(Class.class, String.class).newInstance(getBeanType(), item.getName());
						filter.addFilterListener(event -> {
							if (this.getDataProvider() instanceof ConfigurableFilterDataProvider provider) {
								provider.setFilter(new ArrayList<SerializableFilter<T, ?>>(filters.values()));
							}
						});
						filterRow.getCell(this.getColumnByKey(item.getName()))
								.setComponent(filter.getFilterComponent());
						filters.put(this.getColumnByKey(item.getName()), filter);
					}
				} catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException| InvocationTargetException | NoSuchMethodException e) {
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected void setDefaultFilters() {
		typeToFilter.put( String.class, (Class<?>) GridTextFilter.class);
		typeToFilter.put(Integer.class, (Class<?>)GridNumberFilter.class);
		typeToFilter.put(Long.class, (Class<?>)GridNumberFilter.class);
		typeToFilter.put(Float.class, (Class<?>)GridNumberFilter.class);
		typeToFilter.put(Double.class, (Class<?>)GridNumberFilter.class);
	}
}
