package de.bauersoft.views.offers;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.bauersoft.data.entities.Field;
import de.bauersoft.data.repositories.field.FieldRepository;
import de.bauersoft.data.repositories.menu.MenuRepository;
import de.bauersoft.views.MainLayout;

import java.util.List;


@PageTitle("offers")
@Route(value = "offers_old", layout = MainLayout.class)
@AnonymousAllowed
public class OffersView_OLD extends Div {
	private final FieldRepository fieldRepository;

	public OffersView_OLD(MenuRepository menuRepository, FieldRepository fieldRepository) {
		setClassName("content");

		VerticalLayout pageVerticalLayout = new VerticalLayout();
		pageVerticalLayout.setSizeFull();

		Div comboDiv = new Div();

		ComboBox<Field> fieldComboBox = new ComboBox<>();
		List<Field>	fields =fieldRepository.findAll();
		fieldComboBox.setItems(fields);
		fieldComboBox.setItemLabelGenerator(Field::getName);
		if(!fields.isEmpty()) {
			fieldComboBox.setValue(fields.getFirst());
		}

		comboDiv.add(fieldComboBox);

		Div mainDiv = new Div();
		mainDiv.setWidthFull();

		Span aktuellerZyklus = new Span("Aktueller Zyklus");
		Span naechsterZyklus = new Span("Nächster Zyklus");

		VerticalLayout topVerticalLayout = new VerticalLayout();
		topVerticalLayout.setSizeFull();
		topVerticalLayout.setClassName("horizontal-boxes");
		Button topWeekOne = new Button("Woche 1");
		Button topWeekTwo = new Button("Woche 2");
		topVerticalLayout.add(topWeekOne, topWeekTwo);

		VerticalLayout bottomVerticalLayout = new VerticalLayout();
		bottomVerticalLayout.setSizeFull();
		bottomVerticalLayout.setClassName("horizontal-boxes");
		Button bottomWeekOne = new Button("Woche 1");
		Button bottomWeekTwo = new Button("Woche 2");
		bottomVerticalLayout.add(bottomWeekOne, bottomWeekTwo);

		mainDiv.add(aktuellerZyklus, topVerticalLayout, naechsterZyklus, bottomVerticalLayout);


		pageVerticalLayout.add(comboDiv, mainDiv);
		this.add(pageVerticalLayout);
		this.fieldRepository = fieldRepository;
	}

}
