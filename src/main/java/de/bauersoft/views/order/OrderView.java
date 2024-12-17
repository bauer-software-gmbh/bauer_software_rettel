package de.bauersoft.views.order;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.bauersoft.data.entities.Institution;
import de.bauersoft.data.entities.User;
import de.bauersoft.data.experimental.MonthDataContainer;
import de.bauersoft.data.repositories.institution.InstitutionRepository;
import de.bauersoft.data.repositories.user.UserRepository;
import de.bauersoft.views.MainLayout;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "order", layout = MainLayout.class)
@PageTitle("Bestellen")
@AnonymousAllowed
public class OrderView extends Div
{

	private Grid grid = new Grid();

	private User currentUser;
	private ComboBox<Institution> institutionComboBox;

	public OrderView(InstitutionRepository institutionRepository, UserRepository userRepository)
	{
		setClassName("content");
//		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		if(!(principal instanceof User))
//		{
//			System.out.println("OIDhgdfoijhijgjh9ifkihojhrfm,ffmkripxfdogi,kgilwfögfkh9fnbf9kghb");
//			return;
//		}
//
//		currentUser = (User) principal;

		institutionComboBox = new ComboBox<>();



	}

	//old
//	public OrderView(DatePickerI18n datePickerI18n)
//	{
//		setClassName("content");
//		Grid<MonthDataContainer> grid = new Grid<MonthDataContainer>(MonthDataContainer.class, false);
//		LocalDate date = LocalDate.now().withDayOfMonth(1);
//		grid.addColumn(item -> item.getName()).setFlexGrow(0).setHeader("Field");
//		for (int i = 0; i < date.getMonth().length(date.isLeapYear()); i++) {
//			final int day = Integer.valueOf(i);
//			grid.addColumn(item -> item.getDay(day)).setHeader(datePickerI18n.getWeekdaysShort().get((date.plusDays(i).getDayOfWeek().getValue()+1)%7)+""+date.plusDays(i).format(DateTimeFormatter.ofPattern("dd.MM"))).setFlexGrow(0).setAutoWidth(true);
//		}
//
//		this.add(grid);
//	}
//
//	private class InputField extends NumberField{
//
//		public InputField(Object obj) {
//			super();
//		}
//	}
}
