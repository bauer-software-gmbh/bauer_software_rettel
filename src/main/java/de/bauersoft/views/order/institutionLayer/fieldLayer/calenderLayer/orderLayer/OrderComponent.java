package de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer.orderLayer;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.offer.Offer;
import de.bauersoft.data.entities.order.Order;
import de.bauersoft.data.entities.order.OrderAllergen;
import de.bauersoft.data.entities.order.OrderData;
import de.bauersoft.services.OrderDataService;
import de.bauersoft.services.OrderService;
import de.bauersoft.services.offer.OfferService;
import de.bauersoft.views.order.OrderManager;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer.CalenderComponent;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer.orderLayer.allergenLayer.AllergenComponent;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer.orderLayer.allergenLayer.AllergenMapContainer;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer.orderLayer.variantLayer.VariantComponent;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer.orderLayer.variantLayer.VariantMapContainer;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Getter
public class OrderComponent extends VerticalLayout
{
    private final OrderManager orderManager;
    private final CalenderComponent calenderComponent;
    private final LocalDate localDate;

    private final InstitutionField institutionField;
    private final Institution institution;
    private final Field field;

    private final DateTimeFormatter dateTimeFormatter;

    private final OrderService orderService;
    private final OfferService offerService;
    private final OrderDataService orderDataService;

    private final VariantMapContainer variantMapContainer;
    private final AllergenMapContainer allergenMapContainer;

    private Paragraph paragraph;

    private VariantComponent variantComponent;
    private AllergenComponent allergenComponent;

    private Optional<Order> orderOptional;
    private Order order;
    private Optional<Offer> offerOptional;
    private Offer offer;
    private Optional<Menu> menuOptional;
    private Menu menu;

    public OrderComponent(OrderManager orderManager, CalenderComponent calenderComponent, LocalDate localDate)
    {
        this.orderManager = orderManager;
        this.calenderComponent = calenderComponent;
        this.localDate = localDate;

        institutionField = calenderComponent.getInstitutionField();
        institution = institutionField.getInstitution();
        field = institutionField.getField();

        dateTimeFormatter = CalenderComponent.dateTimeFormatter;

        orderService = orderManager.getOrderService();
        offerService = orderManager.getOfferService();
        orderDataService = orderManager.getOrderDataService();

        variantMapContainer = new VariantMapContainer();
        allergenMapContainer = new AllergenMapContainer();

        paragraph = new Paragraph();

        orderOptional = orderService.findByOrderDateAndInstitutionAndField(localDate, institution, field);
        offerOptional = offerService.findByLocalDateAndField(localDate, field);

        if(offerOptional.isEmpty())
        {
            String text = "Für den " + dateTimeFormatter.format(localDate) + " ist kein Angebot vorhanden.";
            if(orderOptional.isPresent())
                text = "Für den " + dateTimeFormatter.format(localDate) + " ist kein Angebot vorhanden. Es wurde jedoch bereits eine Bestellung aufgegeben. Bitte kontaktieren Sie Rettel, dies ist ein Fehler!";

            paragraph.setText(text);
            this.add(paragraph);
            return;
        }
        offer = offerOptional.get();

        order = orderOptional.orElseGet(() ->
                {
                    Order order = new Order();
                    order.setOrderDate(localDate);
                    order.setInstitution(institution);
                    order.setField(field);

                    return order;
                });

        menuOptional = offer.getMenus().stream().findFirst();
        if(menuOptional.isEmpty())
        {
            String text = "Für den " + dateTimeFormatter.format(localDate) + " ist kein Menü vorhanden.";
            paragraph.setText(text);

            this.add(paragraph);
            return;
        }

        menu = menuOptional.get();

        this.remove(paragraph);

        for(OrderData orderData : order.getOrderData())
            variantMapContainer.addContainer(orderData.getVariant(), orderData, ContainerState.SHOW);

        for(OrderAllergen orderAllergen : order.getOrderAllergens())
            allergenMapContainer.addContainer(allergenMapContainer.nextMapper(), orderAllergen, ContainerState.SHOW);

        variantComponent = new VariantComponent(orderManager, this, menu, order, variantMapContainer);

        allergenComponent = new AllergenComponent(orderManager, this, order, allergenMapContainer);

        this.add(variantComponent, allergenComponent);
        this.getStyle()
                .setPadding("0px");
    }
}
