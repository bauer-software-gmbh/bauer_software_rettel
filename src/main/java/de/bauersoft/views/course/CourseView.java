package de.bauersoft.views.course;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofiltergridOld.AutoFilterGrid;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.providers.CourseDataProvider;
import de.bauersoft.services.ComponentService;
import de.bauersoft.services.CourseService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Menükomponenten")
@Route(value = "course", layout = MainLayout.class)
@RolesAllowed(value = { "ADMIN", "ACCOUNTENT" })
public class CourseView extends Div
{
	private final CourseService courseService;
	private final CourseDataProvider courseDataProvider;
	private final ComponentService componentService;

	AutoFilterGrid<Course> grid = new AutoFilterGrid<>(Course.class, false, true);

	public CourseView(CourseService courseService, CourseDataProvider courseDataProvider, ComponentService componentService)
	{
        this.courseService = courseService;
        this.courseDataProvider = courseDataProvider;
        this.componentService = componentService;

        setClassName("content");

		grid.setDataProvider(courseDataProvider);

		grid.setHeightFull();
		grid.setWidthFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.addColumn("name").setHeader("Name");

		grid.addItemDoubleClickListener(event ->
		{
			new CourseDialog(courseService, courseDataProvider, event.getItem(), DialogState.EDIT);
		});

		GridContextMenu<Course> contextMenu = grid.addContextMenu();
		contextMenu.addItem("Neue Menükomponente", event ->
		{
			new CourseDialog(courseService, courseDataProvider, new Course(), DialogState.NEW);
		});

		GridMenuItem<Course> deleteItem = contextMenu.addItem("Löschen", event ->
		{
			event.getItem().ifPresent(item ->
			{
				if(componentService.getRepository().existsByCourseId(item.getId()))
				{
					Div div = new Div();
					div.setMaxWidth("33vw");
					div.getStyle().set("white-space", "normal");
					div.getStyle().set("word-wrap", "break-word");

					div.add(new Text("Die Menükomponente \"" + item.getName() + "\" kann nicht gelöscht werden, da er noch mit einigen Komponenten verknüpft ist."));

					Notification notification = new Notification(div);
					notification.setDuration(5000);
					notification.setPosition(Notification.Position.MIDDLE);
					notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
					notification.open();

					return;
				}

				courseService.deleteById(item.getId());
				courseDataProvider.refreshAll();
			});
		});

		contextMenu.addGridContextMenuOpenedListener(event ->
		{
			deleteItem.setVisible(event.getItem().isPresent());
		});

		this.add(grid);
	}
}
