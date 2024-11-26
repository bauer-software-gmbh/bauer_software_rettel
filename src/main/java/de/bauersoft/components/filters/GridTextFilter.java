package de.bauersoft.components.filters;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

import de.bauersoft.data.filters.TextFilter;

public class GridTextFilter<T> extends TextFilter<T> implements GridFilter<T, String> {

	private TextField textField = new TextField();
	private List<GridFilterListener<T,String>> listeners = new ArrayList<GridFilterListener<T,String>>();
	public GridTextFilter(Class<T> beanType, String fieldName) {
		super(beanType, fieldName);
		textField.setWidthFull();
		textField.setClearButtonVisible(true);
		textField.addValueChangeListener(valueChangeEvent->{
			super.setValue(valueChangeEvent.getValue());
			FilterChangeEvent<T, String> filterChangeEvent = new FilterChangeEvent<T,String>(this, valueChangeEvent.getValue());
			listeners.forEach(consumer->consumer.filterValueChanged(filterChangeEvent));
			valueChangeEvent.getSource().getStyle().setColor(valueChangeEvent.getValue() != null && !valueChangeEvent.getValue().isBlank() ? "var(--lumo-error-text-color)": "inherit");
		});
	}

	@Override
	public Component getFilterComponent() {
		return textField;
	}

	@Override
	public Registration addFilterListener(GridFilterListener<T,String> listener) {
		return Registration.addAndRemove(listeners, listener);
	}

	@Override
	public void removeFilterListener(GridFilterListener<T,String> listener) {
		listeners.remove(listener);
	}

}
