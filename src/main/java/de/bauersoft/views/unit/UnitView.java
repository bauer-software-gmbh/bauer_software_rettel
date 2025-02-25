package de.bauersoft.views.unit;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.unit.Unit;
import de.bauersoft.data.providers.UnitDataProvider;
import de.bauersoft.services.IngredientService;
import de.bauersoft.services.UnitService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;

import java.util.function.Consumer;

@PageTitle("Einheiten")
@Route(value = "unit", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UnitView extends Div
{
	private final AutoFilterGrid<Unit> grid = new AutoFilterGrid<>(Unit.class, false, true);
	//private final Grid<Unit> grid = new Grid<>(Unit.class, false);
//	private final AutoFilterGridIW<Unit> grid = new AutoFilterGridIW(Unit.class);

	public UnitView(UnitService unitService,
					UnitDataProvider dataProvider,
					IngredientService ingredientService)
	{
		setClassName("content");

		grid.setWidthFull();
		grid.setHeightFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.addColumn("name").setHeader("Name");
		grid.addColumn("shorthand").setHeader("Abkürzung");
		grid.addColumn(unit -> (unit.getParentUnit() == null) ? "" : unit.getParentUnit().getName()).setHeader("Parent");
		grid.addColumn(unit -> (unit.getParentFactor() == 0.0f) ? "" : unit.getParentFactor()).setHeader("Faktor");

		grid.setItems(dataProvider);

//		grid.addFilterColumn(Unit::getName, Unit::getName, "Name");
//		grid.addFilterColumn(Unit::getShorthand, Unit::getShorthand, "Abkürzung");
//		grid.addFilterColumn(unit -> (unit.getParentUnit() == null) ? "" : unit.getParentUnit().getName(), unit -> (unit.getParentUnit() == null) ? "" : unit.getParentUnit().getName(), "Übergeordnete Einheit");
//		grid.addFilterColumn(unit -> String.valueOf(unit.getParentFactor()), unit -> String.valueOf(unit.getParentFactor()), "Faktor");

//		grid.setItems(dataProvider.fetch(new Query<>()).toList());

//		grid.setItems(query ->
//		{
//			return unitService.getRepository().findAll(PageRequest.of(query.getPage(), query.getPageSize())).stream();
//
//		}, query -> (int) unitService.count());
//		grid.applyFilters();

//		Grid.Column<Unit> nameColumn = grid.addColumn(Unit::getName)
//				.setHeader("Name")
//				.setComparator(Unit::getName);
//
//		Grid.Column<Unit> shorthandColumn = grid.addColumn(Unit::getShorthand)
//				.setHeader("Abkürzung")
//				.setComparator(Unit::getShorthand);
//
//		Grid.Column<Unit> parentColumn = grid.addColumn(unit -> (unit.getParentUnit() == null) ? "" : unit.getParentUnit().getName())
//				.setHeader("Übergeordnete Einheit")
//				.setComparator(unit -> (unit.getParentUnit() == null) ? "" : unit.getParentUnit().getName());
//
//		Grid.Column<Unit> parentFactorColumn = grid.addColumn(unit -> String.valueOf(unit.getParentFactor()))
//				.setHeader("Faktor")
//				.setComparator(unit -> String.valueOf(unit.getParentFactor()));
//
//		grid.setDataProvider(dataProvider);
//		UnitFilter unitFilter = new UnitFilter(dataProvider);
//
//		grid.getHeaderRows().clear();
//		HeaderRow headerRow = grid.appendHeaderRow();
//
//		headerRow.getCell(nameColumn).setComponent(createFilterHeader(unitFilter::setName));
//		headerRow.getCell(shorthandColumn).setComponent(createFilterHeader(unitFilter::setShorthand));
//		headerRow.getCell(parentColumn).setComponent(createFilterHeader(unitFilter::setParent));
//		headerRow.getCell(parentFactorColumn).setComponent(createFilterHeader(unitFilter::setFaktor));

		grid.addItemDoubleClickListener(event ->
		{
			new UnitDialog(unitService, dataProvider, event.getItem(), DialogState.EDIT);
		});

		GridContextMenu<Unit> contextMenu = grid.addContextMenu();
		contextMenu.addItem("Neue Einheit", event ->
		{
			new UnitDialog(unitService, dataProvider, new Unit(), DialogState.NEW);
		});

		GridMenuItem<Unit> deleteItem = contextMenu.addItem("Löschen", event ->
		{
			event.getItem().ifPresent(item ->
			{
				if(ingredientService.getRepository().existsByUnitId(item.getId()))
				{
					//TODO später durch fancy dialog ersetzen :3
					Div div = new Div();
					div.setMaxWidth("33vw");
					div.getStyle().set("white-space", "normal");
					div.getStyle().set("word-wrap", "break-word");

					div.add(new Text("Die Einheit " + item.getName() + "(" + item.getShorthand() + ")" + " kann nicht gelöscht werden, da sie von einigen Zutaten verwendet wird."));

					Notification notification = new Notification(div);
					notification.setDuration(5000);
					notification.setPosition(Notification.Position.MIDDLE);
					notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
					notification.open();
					return;
				}

				unitService.deleteById(item.getId());
				dataProvider.refreshAll();
			});
		});

		contextMenu.addGridContextMenuOpenedListener(event ->
		{
			deleteItem.setVisible(event.getItem().isPresent());
		});

		this.add(grid);
	}

//	private static class UnitFilter
//	{
//		private final UnitDataProvider dataView;
//
//		private String name;
//		private String shorthand;
//		private String parent;
//		private String faktor;
//
//		public UnitFilter(UnitDataProvider dataView) {
//			this.dataView = dataView;
//			this.dataView.addFilter(this::test);
//		}
//
//		public void setName(String name) {
//			this.name = name;
//			this.dataView.refreshAll();
//		}
//
//		public void setShorthand(String shorthand) {
//			this.shorthand = shorthand;
//			this.dataView.refreshAll();
//		}
//
//		public void setParent(String parent)
//		{
//			this.parent = parent;
//			this.dataView.refreshAll();
//		}
//
//		public void setFaktor(String faktor)
//		{
//			this.faktor = faktor;
//			this.dataView.refreshAll();
//		}
//
//		public boolean test(Unit unit) {
//			boolean matchesName = matches(unit.getName(), name);
//			boolean matchesShorthand = matches(unit.getShorthand(), shorthand);
//
//			Unit parent = unit.getParentUnit();
//			boolean matchesPrent = matches((parent == null) ? "" : parent.getName(), this.parent);
//
//			boolean matchesFaktor = matches(String.valueOf(unit.getParentFactor()), faktor);
//
//			return matchesName && matchesShorthand && matchesPrent && matchesFaktor;
//		}
//
//		private boolean matches(String value, String searchTerm) {
//			return searchTerm == null || searchTerm.isEmpty()
//					|| value.toLowerCase().contains(searchTerm.toLowerCase());
//		}
//	}
//
//	private static Component createFilterHeader(Consumer<String> filterChangeConsumer) {
//		TextField textField = new TextField();
//		textField.setValueChangeMode(ValueChangeMode.EAGER);
//		textField.setClearButtonVisible(true);
//		textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
//		textField.setWidthFull();
//		textField.getStyle().set("max-width", "100%");
//		textField.addValueChangeListener(
//				e -> filterChangeConsumer.accept(e.getValue()));
//		VerticalLayout layout = new VerticalLayout(textField);
//		layout.getThemeList().clear();
//		layout.getThemeList().add("spacing-xs");
//
//		return layout;
//	}
}
