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
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.components.autofiltergridOld.AutoFilterGrid;
import de.bauersoft.data.entities.component.Component;
import de.bauersoft.data.providers.ComponentDataProvider;
import de.bauersoft.data.repositories.component.ComponentRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.recipe.RecipeRepository;
import de.bauersoft.services.*;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Komponenten")
@Route(value = "component", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "KITCHEN_ADMIN", "OFFICE_ADMIN"})
@Uses(Icon.class)
public class ComponentView extends Div
{
	private final ComponentService componentService;
	private final RecipeService recipeService;
	private final CourseService courseService;
	private final VariantService variantService;
	private final UnitService unitService;

	private final FilterDataProvider<Component, Long> filterDataProvider;
    private final AutofilterGrid<Component, Long> grid;

    public ComponentView(ComponentService componentService, RecipeService recipeService, CourseService courseService, VariantService variantService, UnitService unitService)
    {
        this.componentService = componentService;
        this.recipeService = recipeService;
        this.courseService = courseService;
        this.variantService = variantService;
        this.unitService = unitService;

        setClassName("content");

		filterDataProvider = new FilterDataProvider<>(componentService);
		grid = new AutofilterGrid<>(filterDataProvider);

		grid.setHeightFull();
		grid.setWidthFull();
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.addColumn("name", "Name", Component::getName, false);
		grid.addColumn("description", "Beschreibung", Component::getDescription, false);

       	grid.AutofilterGridContextMenu()
			   .enableGridContextMenu()
			   .enableAddItem("Neue Menükomponente", event ->
			   {
				   new ComponentDialog(filterDataProvider, componentService, recipeService, courseService, unitService, new Component(), DialogState.NEW);

			   }).enableDeleteItem("Löschen", event ->
			   {
				   event.getItem().ifPresent(item ->
				   {
//					   if(variantService.getRepository().existsByComponentsId(item.getId()))
//					   {
//						   Div div = new Div();
//						   div.setMaxWidth("33vw");
//						   div.getStyle().set("white-space", "normal");
//						   div.getStyle().set("word-wrap", "break-word");
//
//						   div.add(new Text("Die Komponente \"" + item.getName() + "\" kann nicht gelöscht werden, da sie noch von einigen Menü-Varianten verwendet wird."));
//
//						   Notification notification = new Notification(div);
//						   notification.setDuration(5000);
//						   notification.setPosition(Notification.Position.MIDDLE);
//						   notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
//						   notification.open();
//						   return;
//					   }

					   componentService.deleteById(item.getId());
					   filterDataProvider.refreshAll();
				   });
			   });

		   grid.addItemDoubleClickListener(event ->
		   {
			   new ComponentDialog(filterDataProvider, componentService, recipeService, courseService, unitService, event.getItem(), DialogState.EDIT);
		   });

        this.add(grid);
    }
}
