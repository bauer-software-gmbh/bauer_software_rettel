package de.bauersoft.components.filters;

public interface GridFilterListener<T,V> {

	public void filterValueChanged(FilterChangeEvent<T,V> filterChangeEvent);

}
