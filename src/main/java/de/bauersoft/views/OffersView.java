package de.bauersoft.views;

import java.time.LocalDate;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.bauersoft.data.entities.Menu;
import de.bauersoft.data.repositories.menu.MenuRepository;


@PageTitle("offers")
@Route(value = "offers", layout = MainLayout.class)
@AnonymousAllowed
public class OffersView extends Div {
	public OffersView(MenuRepository menuRepository) {
		setClassName("content");
		VerticalLayout pageVerticalLayout = new VerticalLayout();
		pageVerticalLayout.setSizeFull();
		DatePicker datePicker = new DatePicker("date");
		datePicker.setValue(LocalDate.now());
		pageVerticalLayout.add(datePicker);
		HorizontalLayout contentHorizontalLayout = new HorizontalLayout();
		contentHorizontalLayout.setSizeFull();
		Div day0 = new Div();
		day0.setClassName("offer_target");
		Div day1 = new Div();
		day1.setClassName("offer_target");
		Div day2 = new Div();
		day2.setClassName("offer_target");
		Div day3 = new Div();
		day3.setClassName("offer_target");
		Div day4 = new Div();
		day4.setClassName("offer_target");
		Div day5 = new Div();
		day5.setClassName("offer_target");
		Div day6 = new Div();
		day6.setClassName("offer_target");
		contentHorizontalLayout.add(day0, day1, day2, day3, day4, day5, day6);
		
		
		VirtualList<Menu> virtualList = new VirtualList<Menu>();
		virtualList.setRenderer(new ComponentRenderer<Div,Menu>(component->{
			Div container = new Div();
			container.add(new Span(component.getName()));
			DragSource.create(container);
			return container;
		}));
		virtualList.setItems(menuRepository.findAll());
		virtualList.setSizeFull();
		contentHorizontalLayout.add(virtualList);
		pageVerticalLayout.add(contentHorizontalLayout);
		this.add(pageVerticalLayout);
		
	}
}
