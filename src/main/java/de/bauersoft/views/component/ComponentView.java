package de.bauersoft.views.component;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.component.Component;
import de.bauersoft.data.providers.ComponentDataProvider;
import de.bauersoft.data.repositories.component.ComponentRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.recipe.RecipeRepository;
import de.bauersoft.services.ComponentService;
import de.bauersoft.services.VariantService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Component")
@Route(value = "component", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class ComponentView extends Div
{
    AutoFilterGrid<Component> grid = new AutoFilterGrid<Component>(Component.class, false, true);

    public ComponentView(ComponentService componentService, ComponentDataProvider dataProvider,
						 RecipeRepository recipeRepository, CourseRepository courseRepository,
						 ComponentRepository componentRepository,
						 VariantService variantService)
    {
        setClassName("content");

		grid.setHeightFull();
		grid.setWidthFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.setDataProvider(dataProvider);

        grid.addColumn("name");
        grid.addColumn("description");

        grid.addColumn(item ->
		{
			return item.getCourse() != null ? item.getCourse().getName() : "";

		}).setHeader("Course");

        grid.addItemDoubleClickListener(event ->
		{
			new ComponentDialog(componentService, dataProvider, recipeRepository, courseRepository, componentRepository, event.getItem(), DialogState.EDIT);
		});

        GridContextMenu<Component> contextMenu = grid.addContextMenu();
        contextMenu.addItem("new component", event ->
		{
			new ComponentDialog(componentService, dataProvider, recipeRepository, courseRepository, componentRepository, new Component(), DialogState.NEW);
		});

		GridMenuItem<Component> deleteItem = contextMenu.addItem("delete", event ->
		{
			event.getItem().ifPresent(item ->
			{
				if(variantService.getRepository().existsByComponentsId(item.getId()))
				{
					Div div = new Div();
					div.setMaxWidth("33vw");
					div.getStyle().set("white-space", "normal");
					div.getStyle().set("word-wrap", "break-word");

					div.add(new Text("Die Komponente \"" + item.getName() + "\" kann nicht gelöscht werden da sie noch von einigen Menü-Varianten verwendet wird."));

					Notification notification = new Notification(div);
					notification.setDuration(5000);
					notification.setPosition(Notification.Position.MIDDLE);
					notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
					notification.open();
					return;
				}

				componentService.deleteById(item.getId());
				dataProvider.refreshAll();
			});
		});

		contextMenu.addGridContextMenuOpenedListener(event ->
		{
			deleteItem.setVisible(event.getItem().isPresent());
		});

        this.add(grid);
    }
}
