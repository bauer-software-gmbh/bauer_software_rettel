package de.bauersoft.components.filters;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.data.filters.SerializableFilter;

public interface GridFilter<T,V> extends SerializableFilter<T, V> {

	public Component getFilterComponent();
	public Registration addFilterListener(GridFilterListener<T,V> listener);
	public void removeFilterListener(GridFilterListener<T,V> listener);
}
