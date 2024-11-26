package de.bauersoft.views.course;

import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.Course;
import de.bauersoft.data.providers.CourseDataProvider;
import de.bauersoft.services.CourseService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Course")
@Route(value = "course", layout = MainLayout.class)
@RolesAllowed(value = { "ADMIN", "ACCOUNTENT" })
public class CourseView extends Div {
	
	AutoFilterGrid<Course> grid = new AutoFilterGrid<Course>(Course.class, false, true);

	public CourseView(CourseService service,  CourseDataProvider dataProvider) {
		setClassName("content");
		grid.addColumn("name");
		grid.addItemDoubleClickListener(event -> new CourseDialog(service,dataProvider, event.getItem(), DialogState.EDIT));
		grid.setDataProvider(dataProvider);
		GridContextMenu<Course> contextMenu = grid.addContextMenu();
		contextMenu.addItem("new", event -> new CourseDialog(service,dataProvider, new Course(), DialogState.NEW));
		contextMenu.addItem("delete", event -> event.getItem().ifPresent(item -> {
			service.delete(item.getId());
			dataProvider.refreshAll();
		}));
		grid.setHeightFull();
		this.add(grid);
	}
}
