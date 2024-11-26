package de.bauersoft.views.order;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.bauersoft.data.experimental.MonthDataContainer;
import de.bauersoft.views.MainLayout;

@Route(value = "order", layout = MainLayout.class)
@AnonymousAllowed
public class OrderView extends Div {
	public OrderView(DatePickerI18n datePickerI18n) {
		setClassName("content");
		Grid<MonthDataContainer> grid = new Grid<MonthDataContainer>(MonthDataContainer.class, false);
		LocalDate date = LocalDate.now().withDayOfMonth(1);
		grid.addColumn(item -> item.getName()).setFlexGrow(0).setHeader("Field");
		for (int i = 0; i < date.getMonth().length(date.isLeapYear()); i++) {
			final int day = Integer.valueOf(i);
			grid.addColumn(item -> item.getDay(day)).setHeader(datePickerI18n.getWeekdaysShort().get((date.plusDays(i).getDayOfWeek().getValue()+1)%7)+""+date.plusDays(i).format(DateTimeFormatter.ofPattern("dd.MM"))).setFlexGrow(0).setAutoWidth(true);
		}
		
		this.add(grid);
	}
	
	private class InputField extends NumberField{
		
		public InputField(Object obj) {
			super();
		}
	}
}
