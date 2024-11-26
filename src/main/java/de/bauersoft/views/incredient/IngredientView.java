package de.bauersoft.views.incredient;

import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.Allergen;
import de.bauersoft.data.entities.Ingredient;
import de.bauersoft.data.providers.IngredientDataProvider;
import de.bauersoft.data.repositories.additive.AdditiveRepository;
import de.bauersoft.data.repositories.allergen.AllergenRepository;
import de.bauersoft.data.repositories.unit.UnitRepository;
import de.bauersoft.services.IngredientService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;


@PageTitle("ingredient")
@Route(value = "incredient", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class IngredientView extends Div {

	private final AutoFilterGrid<Ingredient> grid = new AutoFilterGrid<>(Ingredient.class, false, true);
	public IngredientView(IngredientService service,
			UnitRepository unitRepository,
			AllergenRepository allergenRepository,
		    AdditiveRepository additiveRepository,
		    IngredientDataProvider dataProvider) {
		setClassName("content");
		grid.setDataProvider(dataProvider);
		
		grid.addColumn("name").setResizable(true);
		grid.addColumn("description").setResizable(true);
		grid.addColumn(item -> item.getUnit() == null ? "" :item.getUnit().getName()).setKey("unit.name").setHeader("unit").setResizable(true);
		grid.addComponentColumn(item -> getAllergensComponent(item.getAllergens())).setKey("allergens.name").setHeader("Allergens");
		grid.addComponentColumn(item -> item.getAdditives() == null || item.getAdditives().isEmpty() ? new Span() : VaadinIcon.WARNING.create())
				.setKey("additives.name").setHeader("Additives").setResizable(true);
		
		grid.addItemDoubleClickListener(
				event -> new IngredientDialog(service, unitRepository,allergenRepository,additiveRepository,dataProvider, event.getItem(), DialogState.EDIT));
		GridContextMenu<Ingredient> contextMenu = grid.addContextMenu();
		contextMenu.addItem("new institution",
				event -> new IngredientDialog(service, unitRepository,allergenRepository,additiveRepository,dataProvider, new Ingredient(), DialogState.NEW));
		contextMenu.addItem("delete", event -> event.getItem().ifPresent(item -> {
			service.delete(item.getId());
			dataProvider.refreshAll();
		}));
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.setHeightFull();
		this.add(grid);
		
	}
	
	private Component getAllergensComponent(Set<Allergen> allergens){
		 if(allergens == null || allergens.isEmpty()) return new Span(); 
		 Icon icon =  VaadinIcon.WARNING.create();
		 icon.setTooltipText(allergens.stream().map(item-> item.getName()).collect(Collectors.joining(",")));
		 return icon;
	}
}
