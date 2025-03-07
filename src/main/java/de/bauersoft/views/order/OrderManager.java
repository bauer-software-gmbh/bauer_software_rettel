package de.bauersoft.views.order;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.dom.Style;
import de.bauersoft.data.entities.role.Role;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.repositories.institution.InstitutionRepository;
import de.bauersoft.security.AuthenticatedUser;
import de.bauersoft.services.*;
import de.bauersoft.services.offer.OfferService;
import de.bauersoft.views.order.institutionLayer.InstitutionTabSheet;
import lombok.Getter;

@Getter
public class OrderManager extends Div
{
    private AuthenticatedUser authenticatedUser;
    private final User user;

    private final InstitutionService institutionService;
    private final InstitutionRepository institutionRepository;
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

    private InstitutionTabSheet institutionTabSheet;

    public OrderManager(AuthenticatedUser authenticatedUser, InstitutionService institutionService, FieldService fieldService, MenuService menuService, VariantService variantService, OfferService offerService, AllergenService allergenService, OrderService orderService, OrderDataService orderDataService, OrderAllergenService orderAllergenService, InstitutionPatternService institutionPatternService, InstitutionAllergenService institutionAllergenService)
    {
        this.authenticatedUser = authenticatedUser;
        this.institutionService = institutionService;
        this.institutionRepository = institutionService.getRepository();
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

        if(authenticatedUser.get().isEmpty())
            throw new IllegalArgumentException("AuthenticatedUser cannot be empty!");

        user = authenticatedUser.get().get();

        this.setWidthFull();
        this.setHeightFull();

        if(!getUser().getRoles().contains(Role.ORDER_SHOW_ALL_INSTITUTIONS) &&
                getInstitutionService().findAllByUsersId(getUser().getId()).size() < 1)
        {
            Span div = new Span("Ihr Account ist noch nicht mit einer Institution verknüpft!");
            div.getStyle()
                    .set("margin", "var(--lumo-space-l)");

            this.add(div);
            return;
        }

        this.institutionTabSheet = new InstitutionTabSheet(this);

        this.add(institutionTabSheet);
    }
}
