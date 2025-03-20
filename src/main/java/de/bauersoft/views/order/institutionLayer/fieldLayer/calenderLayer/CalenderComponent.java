package de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.dom.Style;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.services.OrderService;
import de.bauersoft.services.offer.OfferService;
import de.bauersoft.views.order.OrderManager;
import de.bauersoft.views.order.institutionLayer.fieldLayer.FieldTab;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer.orderLayer.OrderComponent;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer.orderLayer.allergenLayer.AllergenMapContainer;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer.orderLayer.variantLayer.VariantMapContainer;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Getter
public class CalenderComponent extends VerticalLayout
{
    public static final DatePicker.DatePickerI18n datePickerI18n;
    public static final DateTimeFormatter dateTimeFormatter;

    static
    {
        datePickerI18n = new DatePicker.DatePickerI18n();
        datePickerI18n.setWeekdays(Arrays.asList("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"))
                .setWeekdaysShort(Arrays.asList("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"))
                .setMonthNames(Arrays.asList("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"))
                .setToday("Heute")
                .setCancel("Abbrechen")
                .setFirstDayOfWeek(0);

        dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ROOT);
    }

    private final OrderManager orderManager;
    private final FieldTab fieldTab;
    private final InstitutionField institutionField;

    private final Institution institution;
    private final Field field;

    private final OrderService orderService;
    private final OfferService offerService;

    private final Map<LocalDate, OrderComponent> orderComponentMap;

    private Paragraph paragraph;

    private HorizontalLayout calenderRow;

    private DatePicker datePicker;

    private TimePicker orderStartTimePicker;
    private TimePicker orderEndTimePicker;

    private Button submitButton;

    public CalenderComponent(OrderManager orderManager, FieldTab fieldTab)
    {
        this.orderManager = orderManager;
        this.fieldTab = fieldTab;

        institutionField = fieldTab.getInstitutionField();

        institution = institutionField.getInstitution();
        field = institutionField.getField();

        paragraph = new Paragraph();

        orderService = orderManager.getOrderService();
        offerService = orderManager.getOfferService();

        orderComponentMap = new HashMap<>();

        calenderRow = new HorizontalLayout();

        datePicker = new DatePicker("Bestelldatum");
        datePicker.setI18n(datePickerI18n);

        orderStartTimePicker = new TimePicker("Bestellzeitraum von", institution.getOrderStart());
        orderStartTimePicker.setReadOnly(true);
        orderStartTimePicker.getStyle()
                .setMaxWidth("10em")
                .set("tabindex", "-1");
        orderStartTimePicker.getElement()
                .setAttribute("tabindex", "-1");

        orderEndTimePicker = new TimePicker(" bis ", institution.getOrderEnd());
        orderEndTimePicker.setReadOnly(true);
        orderEndTimePicker.getStyle()
                .setMaxWidth("10em")
                .set("tabindex", "-1");
        orderEndTimePicker.getElement()
                .setAttribute("tabindex", "-1");

        calenderRow.add(datePicker, orderStartTimePicker, orderEndTimePicker);

        submitButton = new Button("Bestellung für " + field.getName() + " abschicken!");
        submitButton.getStyle()
                .setPosition(Style.Position.FIXED)
                .setBottom("var(--lumo-space-s)")
                .setRight("var(--lumo-space-s)");

        this.add(calenderRow, submitButton);

        datePicker.addValueChangeListener(event ->
        {
            submitButton.setVisible(true);

            OrderComponent orderComponent = orderComponentMap.get(event.getOldValue());
            if(orderComponent != null)
                this.remove(orderComponent);

            orderComponent = orderComponentMap.computeIfAbsent(event.getValue(), localDate ->
            {
                return new OrderComponent(orderManager, this, localDate);
            });

            if(orderComponent.getOfferOptional().isEmpty() || orderComponent.getMenuOptional().isEmpty())
                submitButton.setVisible(false);

            fieldTab.getTab()
                    .getStyle()
                    .setColor("green")
                    .setFontWeight(Style.FontWeight.BOLD);

            if(orderComponent.getOrderOptional().isEmpty())
            {
                fieldTab.getTab().getStyle()
                        .remove("color")
                        .remove("font-weight");
            }

            this.add(orderComponent);
        });

        datePicker.setValue(LocalDate.now());

        submitButton.addClickListener(event ->
        {
            for(OrderComponent orderComponent : orderComponentMap.values())
            {
                orderService.update(orderComponent.getOrder());

                VariantMapContainer variantMapContainer = orderComponent.getVariantMapContainer();
                variantMapContainer.acceptTemporaries();
                variantMapContainer.evaluate(container ->
                {
                    container.getEntity().getId().setOrderId(orderComponent.getOrder().getId());
                });

                variantMapContainer.run(orderManager.getOrderDataService());

                AllergenMapContainer allergenMapContainer = orderComponent.getAllergenMapContainer();
                allergenMapContainer.acceptTemporaries();
                allergenMapContainer.run(orderManager.getOrderAllergenService());
            }

            fieldTab.getTab()
                    .getStyle()
                    .setColor("green")
                    .setFontWeight(Style.FontWeight.BOLD);

            Notification notification = new Notification();
            notification.setText("Die Bestellung für " + field.getName() + "wurde gespeichert.");
            notification.setPosition(Notification.Position.MIDDLE);
            notification.setDuration(5000);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.open();
        });

        this.setWidthFull();
        this.setHeightFull();
    }
}
