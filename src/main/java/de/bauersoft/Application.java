package de.bauersoft;

import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;

import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.bauersoft.test.Mapper;
import de.bauersoft.test.TestClass;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "rettels")
@Push
@EnableScheduling
public class Application extends SpringBootServletInitializer implements AppShellConfigurator
{

	public static void main(String[] args)
	{
		SpringApplication.run(Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {    return builder.sources(Application.class);}
	
	@Bean
	DatePickerI18n getDatePickerI18n() {
		DatePickerI18n datePickerI18n = new DatePickerI18n();
		
		   DateFormatSymbols dfs = DateFormatSymbols.getInstance(Locale.GERMAN);

		   datePickerI18n.setMonthNames(List.of(dfs.getMonths()));
		   datePickerI18n.setWeekdays(List.of(dfs.getWeekdays()));
		   datePickerI18n.setWeekdaysShort(List.of(dfs.getShortWeekdays()));
		   datePickerI18n.setDateFormat("yyyy");

		    DayOfWeek firstDayOfWeek = WeekFields.of(Locale.GERMAN).getFirstDayOfWeek();
		    datePickerI18n.setFirstDayOfWeek(firstDayOfWeek.getValue() % 7);
		return datePickerI18n;
	}
}
