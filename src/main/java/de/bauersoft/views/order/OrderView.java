package de.bauersoft.views.order;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.security.AuthenticatedUser;
import de.bauersoft.services.*;
import de.bauersoft.services.offer.OfferService;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Bestellübersicht")
@Route(value = "order", layout = MainLayout.class)
@RolesAllowed(value = {"ADMIN", "ORDERER"})
public class OrderView extends Div
{

    private final OrderManager orderManager;

    private final AuthenticatedUser authenticatedUser;
    private final InstitutionService institutionService;
    private final FieldService fieldService;
    private final MenuService menuService;
    private final VariantService variantService;
    private final OfferService offerService;
    private final AllergenService allergenService;
    private final OrderService orderService;
    private final OrderDataService orderDataService;
    private final OrderAllergenService orderAllergenService;
    private final InstitutionPatternService institutionPatternService;
    private final InstitutionAllergenService institutionAllergenService;

    public OrderView(AuthenticatedUser authenticatedUser, InstitutionService institutionService, FieldService fieldService, MenuService menuService, VariantService variantService, OfferService offerService, AllergenService allergenService, OrderService orderService, OrderDataService orderDataService, OrderAllergenService orderAllergenService, InstitutionPatternService institutionPatternService, InstitutionAllergenService institutionAllergenService)
    {
        this.authenticatedUser = authenticatedUser;
        this.institutionService = institutionService;
        this.fieldService = fieldService;
        this.menuService = menuService;
        this.variantService = variantService;
        this.offerService = offerService;
        this.allergenService = allergenService;
        this.orderService = orderService;
        this.orderDataService = orderDataService;
        this.orderAllergenService = orderAllergenService;
        this.institutionPatternService = institutionPatternService;
        this.institutionAllergenService = institutionAllergenService;

        setClassName("content");

        this.setHeightFull();
        this.setWidthFull();
        this.getStyle().setBackgroundColor("var(--lumo-base-color)");

        orderManager = new OrderManager(authenticatedUser, institutionService, fieldService, menuService, variantService, offerService, allergenService, orderService, orderDataService, orderAllergenService, institutionPatternService, institutionAllergenService);
        this.add(orderManager);
    }
}
