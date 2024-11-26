package de.bauersoft.data.experimental;

import java.time.LocalDate;
import java.time.Month;

public class MonthDataContainer {
	
	private Month month;
	private LocalDate date;
	private Object[] days;
	private String name;
	
	public MonthDataContainer(LocalDate date) {
		this.date = date.withDayOfMonth(1);
		this.month = date.getMonth();
		days = new Object[this.month.length(date.isLeapYear())];
	}
	
	public int getDayCount(){
		return this.month.length(date.isLeapYear());
	}
	
	public Object getDay(int i) {
		if (i >= getDayCount()) throw new IllegalArgumentException("This month has only "+getDayCount()+" days!"); 
		return days[i];
	}

	public String getName() {
		return this.name;
	}
}
