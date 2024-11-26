package de.bauersoft.components.filters;

public class FilterChangeEvent<T,V> {
	
	private final GridFilter<T,V> gridFilter;
	private final V value;
	
	public FilterChangeEvent(GridFilter<T, V> gridFilter, V value) {
		this.gridFilter = gridFilter;
		this.value = value;
	}
	
	public GridFilter<T, V> getGridFilter() {
		return gridFilter;
	}
	
	public V getValue() {
		return value;
	}
}
