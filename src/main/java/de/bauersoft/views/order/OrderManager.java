package de.bauersoft.views.order;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.repositories.institution.InstitutionRepository;
import de.bauersoft.security.AuthenticatedUser;
import de.bauersoft.services.*;
import de.bauersoft.services.offer.OfferService;
import de.bauersoft.views.order.institutionLayer.InstitutionTabSheet;
import de.bauersoft.views.order.institutionLayer.fieldLayer.FieldTabSheet;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calendarLayer.CalendarCluster;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calendarLayer.allergenLayer.AllergenComponent;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calendarLayer.variantLayer.VariantBoxComponent;
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

    private final InstitutionTabSheet institutionTabSheet;

    private final Button saveButton;
    private final Button cancelButton;
    private final HorizontalLayout buttonLayout;

    public OrderManager(AuthenticatedUser authenticatedUser, InstitutionService institutionService, FieldService fieldService, MenuService menuService, VariantService variantService, OfferService offerService, AllergenService allergenService, OrderService orderService, OrderDataService orderDataService, OrderAllergenService orderAllergenService)
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

        if(authenticatedUser.get().isEmpty())
            throw new IllegalArgumentException("AuthenticatedUser cannot be empty!");

        user = authenticatedUser.get().get();

        this.setWidthFull();
        this.setHeightFull();

        this.institutionTabSheet = new InstitutionTabSheet(this);

        saveButton = new Button("Bestellung abschicken!");

        saveButton.addClickListener(event ->
        {
            if(!institutionTabSheet.validate())
                return;

            try
            {
                institutionTabSheet.save();

                Notification notification = new Notification();
                notification.setText("Die Bestellung wurde erfolgreich gespeichert!");
                notification.setPosition(Notification.Position.MIDDLE);
                notification.setDuration(5000);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                notification.open();

            }catch(Exception e)
            {
                Notification.show("Es ist ein Fehler aufgetreten!", 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);

                e.printStackTrace();
            }
        });

        cancelButton = new Button("Abbrechen");
        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.setMinWidth("150px");
        cancelButton.setMaxWidth("180px");

        buttonLayout = new HorizontalLayout(saveButton);

        buttonLayout.setWidth("50vw");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.getStyle()
                .set("position", "absolute")
                .set("bottom", "20px")
                .set("right", "20px");

        this.add(institutionTabSheet, buttonLayout);
    }
}
