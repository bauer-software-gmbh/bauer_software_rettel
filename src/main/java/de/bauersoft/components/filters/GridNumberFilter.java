package de.bauersoft.components.filters;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.data.filters.NumberFilter;
import de.bauersoft.data.filters.NumericOperations;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.ArrayList;
import java.util.List;

@CssImport(value = "./themes/rettels/components/grid-filter.css")
public class GridNumberFilter<T> extends NumberFilter<T, Double> implements GridFilter<T, Double> {
	
	private Div container = new Div();
	private VerticalLayout verticalLayout = new VerticalLayout();
	private Button toggleButton = new Button();
	private boolean open;
	private ComboBox<NumericOperations> operationComboBox;
	private NumberField firstNumberField = new NumberField();
	private NumberField secondNumberField = new NumberField();
	private List<GridFilterListener<T, Double>> listeners = new ArrayList<GridFilterListener<T, Double>>();

	public GridNumberFilter(Class<T> beanType, String fieldName) {
		super(beanType, fieldName);
		
		toggleButton.setIcon(LineAwesomeIcon.FILTER_SOLID.create());
		toggleButton.setClassName("toggle");
		toggleButton.addClickListener(clickEvent->{
			open = !open;
			verticalLayout.getElement().setAttribute("open", open);
		});
		
		
		operationComboBox = new ComboBox<NumericOperations>();
		operationComboBox.setItemLabelGenerator(item -> item.getValue());
		operationComboBox.setItems(NumericOperations.values());
		operationComboBox.setValue(NumericOperations.Equals);
		operationComboBox.addValueChangeListener(valueChangeEvent->{
			secondNumberField.setVisible(NumericOperations.Between.equals(valueChangeEvent.getValue()));
			super.setMatchType(valueChangeEvent.getValue());
			FilterChangeEvent<T, Double> filterChangeEvent = new FilterChangeEvent<T, Double>(this,null);
			listeners.forEach(consumer -> consumer.filterValueChanged(filterChangeEvent));
			});
		
		firstNumberField.setWidthFull();
		firstNumberField.setClearButtonVisible(true);
		firstNumberField.addValueChangeListener(valueChangeEvent -> {
			super.setFirstValue(valueChangeEvent.getValue());
			FilterChangeEvent<T, Double> filterChangeEvent = new FilterChangeEvent<T, Double>(this,
					valueChangeEvent.getValue());
			listeners.forEach(consumer -> consumer.filterValueChanged(filterChangeEvent));
			valueChangeEvent.getSource().getStyle()
					.setColor(valueChangeEvent.getValue() != null ? "var(--lumo-error-text-color)" : "inherit");
		});
		
		secondNumberField.setWidthFull();
		secondNumberField.setClearButtonVisible(true);
		secondNumberField.addValueChangeListener(valueChangeEvent -> {
			super.setSecondValue(valueChangeEvent.getValue());
			FilterChangeEvent<T, Double> filterChangeEvent = new FilterChangeEvent<T, Double>(this,
					valueChangeEvent.getValue());
			listeners.forEach(consumer -> consumer.filterValueChanged(filterChangeEvent));
			valueChangeEvent.getSource().getStyle()
					.setColor(valueChangeEvent.getValue() != null ? "var(--lumo-error-text-color)" : "inherit");
		});
		secondNumberField.setVisible(false);
		verticalLayout.setClassName("grid-filter");
		verticalLayout.add(operationComboBox,firstNumberField,secondNumberField);
		container.add(toggleButton,verticalLayout);
	}

	@Override
	public Component getFilterComponent() {
		return container;
	}

	@Override
	public Registration addFilterListener(GridFilterListener<T, Double> listener) {
		return Registration.addAndRemove(listeners, listener);
	}

	@Override
	public void removeFilterListener(GridFilterListener<T, Double> listener) {
		listeners.remove(listener);
	}
}
