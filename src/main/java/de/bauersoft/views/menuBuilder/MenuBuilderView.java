package de.bauersoft.views.menuBuilder;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.providers.MenuDataProvider;
import de.bauersoft.data.repositories.component.ComponentRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.menu.MenuPatternComponentsRepository;
import de.bauersoft.data.repositories.menu.MenuRepository;
import de.bauersoft.data.repositories.pattern.PatternRepository;
import de.bauersoft.data.repositories.recipe.RecipeRepository;
import de.bauersoft.services.MenuService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Menue Builder")
@Route(value = "menubuilder", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class MenuBuilderView extends Div
{
    private AutoFilterGrid<Menu> menuGrid = new AutoFilterGrid<>(Menu.class, false, true);

    public MenuBuilderView(MenuService menuService, MenuRepository menuRepository, CourseRepository courseRepository,
                           ComponentRepository componentRepository, PatternRepository patternRepository,
                           MenuDataProvider menuDataProvider,
                           RecipeRepository recipeRepository, MenuPatternComponentsRepository menuPatternComponentsRepository)
    {
        setClassName("content");
        menuGrid.addColumn("name");
        menuGrid.addColumn("description");
        //menuGrid.addColumn("courses");

        menuGrid.addItemDoubleClickListener(event ->

           new MenuBuilderDialog(menuService, menuRepository, courseRepository, componentRepository, patternRepository,
                   menuDataProvider, event.getItem(), DialogState.EDIT,
                   recipeRepository, menuPatternComponentsRepository)
        );

        menuGrid.setDataProvider(menuDataProvider);

        GridContextMenu<Menu> contextMenu = menuGrid.addContextMenu();
        contextMenu.addItem("new", event ->

            new MenuBuilderDialog(menuService, menuRepository, courseRepository, componentRepository, patternRepository,
                    menuDataProvider, new Menu(), DialogState.NEW,
                    recipeRepository, menuPatternComponentsRepository)
        );

        contextMenu.addItem("delete", event ->
        {
            event.getItem().ifPresent(menu ->
            {
                menuService.delete(menu.getId());
                menuDataProvider.refreshAll();
            });
        });

        menuGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        menuGrid.setHeightFull();

        this.add(menuGrid);
    }
}
