package de.bauersoft.views.menuBuilder;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.components.autofiltergridOld.AutoFilterGrid;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.order.OrderData;
import de.bauersoft.data.entities.pattern.Pattern;
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
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

import java.util.stream.Collectors;


//@PageTitle("Menü-Baukasten")
//@Route(value = "menubuilder", layout = MainLayout.class)
//@RolesAllowed({"ADMIN", "KITCHEN_ADMIN", "OFFICE_ADMIN"})
public class MenuBuilderView extends Div
{
    private final MenuService menuService;
    private final CourseService courseService;
    private final ComponentService componentService;
    private final PatternService patternService;
    private final RecipeService recipeService;
    private final VariantService variantService;
    private final FleshService fleshService;
    private final OfferService offerService;
    private final OrderDataService orderDataService;

    private final FilterDataProvider<Menu, Long> filterDataProvider;
    private final AutofilterGrid<Menu, Long> grid;

    public MenuBuilderView(MenuService menuService, CourseService courseService, ComponentService componentService, PatternService patternService, RecipeService recipeService, VariantService variantService, FleshService fleshService, OfferService offerService, OrderDataService orderDataService)
    {
        this.menuService = menuService;
        this.courseService = courseService;
        this.componentService = componentService;
        this.patternService = patternService;
        this.recipeService = recipeService;
        this.variantService = variantService;
        this.fleshService = fleshService;
        this.offerService = offerService;
        this.orderDataService = orderDataService;


        setClassName("content");

        filterDataProvider = new FilterDataProvider<>(menuService);
        grid = new AutofilterGrid<>(filterDataProvider);

        grid.setHeightFull();
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn("name", "Name", Menu::getName, false);
        grid.addColumn("flesh", "Fleischsorte", menu ->
        {
            return (menu.getFlesh() != null) ?
                    menu.getFlesh().getName() :
                    "";

        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.like(criteriaBuilder.lower(path.get("name").as(String.class)), filterInput.toLowerCase() + "%");
        });

        grid.addColumn("variants", "Ernährungsformen", menu ->
        {
            return menu.getVariants().stream().map(Variant::getPattern).map(Pattern::getName).collect(Collectors.joining(", "));

        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            Join<Menu, Variant> variantJoin = root.join("variants", JoinType.LEFT);
            Join<Variant, Pattern> patternJoin = variantJoin.join("pattern", JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(patternJoin.get("name")), filterInput.toLowerCase() + "%");
        }).enableSorting(false);

        grid.AutofilterGridContextMenu()
                        .enableGridContextMenu()
                        .enableAddItem("Neues Menü", event ->
                        {
                            new MenuBuilderDialog(filterDataProvider, menuService, courseService, componentService, patternService, recipeService, variantService, fleshService, offerService, orderDataService, new Menu(), DialogState.NEW);

                        }).enableDeleteItem("Löschen", event ->
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

                                filterDataProvider.refreshAll();
                            });
                        });

        grid.addItemDoubleClickListener(event ->
        {
            new MenuBuilderDialog(filterDataProvider, menuService, courseService, componentService, patternService, recipeService, variantService, fleshService, offerService, orderDataService, event.getItem(), DialogState.NEW);
        });

        this.add(grid);
    }
}
