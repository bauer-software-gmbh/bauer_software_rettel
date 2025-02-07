package de.bauersoft.views.order.old;

import com.vaadin.flow.component.grid.Grid;
import de.bauersoft.data.entities.menu.Menu;
import lombok.Getter;

@Getter
public class MenuGrid extends Grid<Menu>
{
//    private final OrderManager orderManager;
//    private final CalendarCluster calendarCluster;
//    private final InstitutionField institutionField;
//
//    private final List<Menu> menus;
//    private final Order order;
//
//    private final Map<Menu, VariantBoxLayout> variantBoxLayoutMap;
//    private final Map<Menu, AllergenComponent> allergenComponentMap;
//
//    public MenuGrid(OrderManager orderManager, CalendarCluster calendarCluster, InstitutionField institutionField, List<Menu> menus, Order order)
//    {
//        super(Menu.class, false);
//
//        this.orderManager = orderManager;
//        this.calendarCluster = calendarCluster;
//        this.institutionField = institutionField;
//        this.menus = menus;
//        this.order = order;
//
//        variantBoxLayoutMap = new HashMap<>();
//        allergenComponentMap = new HashMap<>();
//
//        this.setWidthFull();
//        this.setHeightFull();
//        this.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
//        this.getStyle()
//                .setMarginTop("0")
//                .setPadding("0");
//
//        //this.getElement().executeJs("this.shadowRoot.querySelector('table').style.overflow = 'clip';");
//
//        this.addColumn(createRenderer(this))
//                .setWidth("80px")
//                .setFlexGrow(0)
//                .setFrozen(true);
//
//        this.addColumn(Menu::getName).setHeader("Menü");
//
//        this.addColumn(menu ->
//        {
//            return menu.getVariants()
//                    .stream()
//                    .map(Variant::getPattern)
//                    .map(Pattern::getName)
//                    .collect(Collectors.joining(", "));
//        }).setHeader("Ernährungsformen");
//
//        this.setDetailsVisibleOnClick(false);
//        this.setItemDetailsRenderer(createDetailsRenderer());
//
//        this.setItems(menus);
//
////        for(OrderData orderData : order.getOrderData())
////        {
////            Variant variant = orderData.getVariant();
////            Menu menu = variant.getMenu();
////
////            if(menu == null) continue;
////
////        }
////
////        for(OrderAllergen orderAllergen : order.getOrderAllergens())
////        {
////        }
//    }
//
//    private Renderer<Menu> createDetailsRenderer()
//    {
//        return new ComponentRenderer<>(menu ->
//        {
//            return new MenuGridRenderedLayout(orderManager, this, menu);
//        });
//    }
//
//    private Renderer<Menu> createRenderer(Grid<Menu> grid)
//    {
//        return LitRenderer
//                .<Menu> of("""
//                <vaadin-button
//                    theme="tertiary icon"
//                    aria-label="Toggle details"
//                    aria-expanded="${model.detailsOpened ? 'true' : 'false'}"
//                    @click="${handleClick}"
//                >
//                    <vaadin-icon
//                    .icon="${model.detailsOpened ? 'lumo:angle-down' : 'lumo:angle-right'}"
//                    ></vaadin-icon>
//                </vaadin-button>
//            """)
//                .withFunction("handleClick",
//                        person -> grid.setDetailsVisible(person,
//                                !grid.isDetailsVisible(person)));
//    }

}
