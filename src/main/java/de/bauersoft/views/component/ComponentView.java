package de.bauersoft.views.component;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.Component;
import de.bauersoft.data.providers.ComponentDataProvider;
import de.bauersoft.data.repositories.component.ComponentRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.recipe.RecipeRepository;
import de.bauersoft.services.ComponentService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Component")
@Route(value = "component", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class ComponentView extends Div {
	AutoFilterGrid<Component> grid = new AutoFilterGrid<Component>(Component.class, false, true);

	public ComponentView(ComponentService componentService, ComponentDataProvider dataProvider,
			RecipeRepository recipeRepository, CourseRepository courseRepository,
			ComponentRepository componentRepository) {
		setClassName("content");
		grid.addColumn("name");
		grid.addColumn("description");
		grid.addColumn(item -> item.getCourse() != null ? item.getCourse().getName() : "").setHeader("Course");
		grid.addItemDoubleClickListener(event -> new ComponentDialog(componentService, dataProvider, recipeRepository,
				courseRepository, componentRepository, event.getItem(), DialogState.EDIT));
		grid.setDataProvider(dataProvider);
		grid.setHeightFull();
		GridContextMenu<Component> contextMenu = grid.addContextMenu();
		contextMenu.addItem("new component", event -> new ComponentDialog(componentService, dataProvider,
				recipeRepository, courseRepository, componentRepository, new Component(), DialogState.NEW));
		contextMenu.addItem("delete", event -> event.getItem().ifPresent(item -> {
			componentService.delete(item.getId());
			dataProvider.refreshAll();
		}));
		grid.setHeightFull();
		this.add(grid);
	}
}
