package de.bauersoft.views.menuBuilder;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofiltergrid.AutoFilterGrid;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.variant.Variant;
import de.bauersoft.data.providers.MenuDataProvider;
import de.bauersoft.data.repositories.component.ComponentRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.menu.MenuRepository;
import de.bauersoft.data.repositories.menuBuilder.MBComponentRepository;
import de.bauersoft.data.repositories.menuBuilder.MBMenuRepository;
import de.bauersoft.data.repositories.menuBuilder.MBPatternRepository;
import de.bauersoft.data.repositories.pattern.PatternRepository;
import de.bauersoft.data.repositories.recipe.RecipeRepository;
import de.bauersoft.services.*;
import de.bauersoft.services.offer.OfferService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

import java.util.stream.Collectors;


@PageTitle("Menü-Baukasten")
@Route(value = "menubuilder", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "KUCHE_ADMIN", "OFFICE_ADMIN"})
public class MenuBuilderView extends Div
{
    private AutoFilterGrid<Menu> menuGrid = new AutoFilterGrid<>(Menu.class, false, true);

    public MenuBuilderView(MenuService menuService, MenuRepository menuRepository, CourseRepository courseRepository,
                           ComponentRepository componentRepository, PatternRepository patternRepository,
                           MenuDataProvider menuDataProvider,
                           RecipeRepository recipeRepository, MBMenuRepository mbMenuRepository,
                           MBComponentRepository mbComponentRepository, MBPatternRepository mbPatternRepository,
                           VariantService variantService, FleshService fleshService,
                           OfferService offerService, OrderDataService orderDataService)
    {
        setClassName("content");
        menuGrid.addColumn("name");
        //menuGrid.addColumn("courses");

        menuGrid.addItemDoubleClickListener(event ->

           new MenuBuilderDialog(menuService, menuRepository, courseRepository, componentRepository, patternRepository,
                   menuDataProvider, event.getItem(), DialogState.EDIT,
                   recipeRepository, variantService, fleshService, orderDataService)
        );

        menuGrid.setDataProvider(menuDataProvider);

        GridContextMenu<Menu> contextMenu = menuGrid.addContextMenu();
        contextMenu.addItem("new", event ->

            new MenuBuilderDialog(menuService, menuRepository, courseRepository, componentRepository, patternRepository,
                    menuDataProvider, new Menu(), DialogState.NEW,
                    recipeRepository, variantService, fleshService, orderDataService)
        );

        contextMenu.addItem("delete", event ->
        {
            event.getItem().ifPresent(item ->
            {
                boolean cancel = false;
                if(offerService.existsByMenusId(item.getId()))
                {
                    Div div = new Div();
                    div.setMaxWidth("33vw");
                    div.getStyle().set("white-space", "normal");
                    div.getStyle().set("word-wrap", "break-word");

                    div.add(new Text("Das Menu " + item.getName() + " kann nicht gelöscht werden da es in einigen Angeboten eingeplant ist."));

                    Notification notification = new Notification(div);
                    notification.setDuration(5000);
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                    notification.open();
                    cancel = true;
                }

                if(orderDataService.existsAnyByVariantIds(item.getVariants().stream().map(Variant::getId).collect(Collectors.toSet())))
                {
                    Div div = new Div();
                    div.setMaxWidth("33vw");
                    div.getStyle().set("white-space", "normal");
                    div.getStyle().set("word-wrap", "break-word");

                    div.add(new Text("Das Menu " + item.getName() + " kann nicht gelöscht werden da einige seiner Varianten in Bestellungen verwendet werden."));

                    Notification notification = new Notification(div);
                    notification.setDuration(5000);
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                    notification.open();
                    cancel = true;
                }

                if(cancel) return;

                String message = "";
                for(int i = 0; i < 100; i++)
                {
                    message += "-";
                }

                message += "1 - ID: " + item.getId();
                System.out.println(message);

                variantService.deleteAllByMenuId(item.getId());
                menuService.deleteById(item.getId());

                menuDataProvider.refreshAll();
            });
        });

        menuGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        menuGrid.setHeightFull();

        this.add(menuGrid);
    }
}
