package de.bauersoft.views.menuBuilder;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.Menu;
import de.bauersoft.data.providers.MenuDataProvider;
import de.bauersoft.data.repositories.component.ComponentRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.menu.MenuRepository;
import de.bauersoft.data.repositories.menuBuilder.MBComponentRepository;
import de.bauersoft.data.repositories.menuBuilder.MBMenuRepository;
import de.bauersoft.data.repositories.menuBuilder.MBPatternRepository;
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
                           RecipeRepository recipeRepository, MBMenuRepository mbMenuRepository,
                           MBComponentRepository mbComponentRepository, MBPatternRepository mbPatternRepository)
    {
        setClassName("content");
        menuGrid.addColumn("name");
        menuGrid.addColumn("description");
        //menuGrid.addColumn("courses");

        menuGrid.addItemDoubleClickListener(event ->

           new MenuBuilderDialog(menuService, menuRepository, courseRepository, componentRepository, patternRepository,
                   menuDataProvider, event.getItem(), DialogState.EDIT,
                   recipeRepository, mbMenuRepository, mbComponentRepository, mbPatternRepository)
        );

        menuGrid.setDataProvider(menuDataProvider);

        GridContextMenu<Menu> contextMenu = menuGrid.addContextMenu();
        contextMenu.addItem("new", event ->

            new MenuBuilderDialog(menuService, menuRepository, courseRepository, componentRepository, patternRepository,
                    menuDataProvider, new Menu(), DialogState.NEW,
                    recipeRepository, mbMenuRepository, mbComponentRepository, mbPatternRepository)
        );

        contextMenu.addItem("delete", event ->
        {
            event.getItem().ifPresent(menu ->
            {
                mbMenuRepository.deleteByMenuId(menu.getId());

                menuRepository.delete(menu);
                menuDataProvider.refreshAll();
            });
        });

        menuGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        menuGrid.setHeightFull();

        this.add(menuGrid);
    }
}
