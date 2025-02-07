package de.bauersoft.views.order.institutionLayer.fieldLayer.calendarLayer.variantLayer;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.dom.Style;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.order.Order;
import de.bauersoft.data.entities.order.OrderData;
import de.bauersoft.data.entities.order.OrderDataKey;
import de.bauersoft.data.entities.variant.Variant;
import de.bauersoft.views.order.OrderManager;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calendarLayer.CalendarCluster;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class VariantBoxComponent extends HorizontalLayout
{
    private final OrderManager orderManager;
    private final InstitutionField institutionField;
    private final CalendarCluster calendarCluster;
    private final Menu menu;

    private final Order order;

    private final Map<Variant, VariantBox> variantBoxMap;

    public VariantBoxComponent(OrderManager orderManager, InstitutionField institutionField, CalendarCluster calendarCluster, Menu menu, Order order)
    {
        Objects.requireNonNull(orderManager, "OrderManager cannot be null!");
        Objects.requireNonNull(institutionField, "InstitutionField cannot be null!");
        Objects.requireNonNull(calendarCluster, "CalendarCluster cannot be null!");
        Objects.requireNonNull(menu, "Menu cannot be null!");
        Objects.requireNonNull(order, "Order cannot be null!");

        this.orderManager = orderManager;
        this.institutionField = institutionField;
        this.calendarCluster = calendarCluster;
        this.menu = menu;
        this.order = order;

        variantBoxMap = new HashMap<>();

        Map<Variant, OrderData> mappedOrderData = new HashMap<>();
        for(OrderData orderData : order.getOrderData())
            mappedOrderData.put(orderData.getVariant(), orderData);

        for(Variant variant : menu.getVariants())
        {
            OrderData orderData = mappedOrderData.get(variant);
            if(orderData == null)
            {
                OrderDataKey orderDataKey = new OrderDataKey();
                orderDataKey.setVariantId(variant.getId());

                orderData = new OrderData();
                orderData.setId(orderDataKey);
                orderData.set_order(order);
                orderData.setVariant(variant);

                mappedOrderData.put(variant, orderData);
            }

            addVariantBox(orderData);
        }

        this.setWidthFull();
        this.getStyle()
                .setFlexWrap(Style.FlexWrap.WRAP)
                .set("gap", "1em");
    }

    private void addVariantBox(OrderData orderData)
    {
        if(variantBoxMap.containsKey(orderData.getVariant())) return;

        VariantBox variantBox = new VariantBox(orderManager, this, institutionField, orderData);

        variantBoxMap.put(orderData.getVariant(), variantBox);
        this.add(variantBox);
    }

    public boolean validate()
    {
        boolean allValid = true;
        for(VariantBox variantBox : variantBoxMap.values())
        {
            if(!variantBox.validate())
                allValid = false;
        }

        return allValid;
    }

    public void save()
    {
        variantBoxMap.values().forEach(VariantBox::save);
    }

    @Getter
    public class VariantBox extends Div
    {
        private final OrderManager orderManager;
        private final VariantBoxComponent variantBoxLayout;
        private final InstitutionField institutionField;

        private final OrderData orderData;

        private final Div nameDiv;
        private final Div descriptionDiv;
        private final NumberField amountField;

        private final Binder<NumberField> amountBinder;

        public VariantBox(OrderManager orderManager, VariantBoxComponent variantBoxLayout, InstitutionField institutionField, OrderData orderData)
        {
            this.orderManager = orderManager;
            this.variantBoxLayout = variantBoxLayout;
            this.institutionField = institutionField;
            this.orderData = orderData;

            nameDiv = new Div();
            nameDiv.setText(orderData.getVariant().getPattern().getName());
            nameDiv.getElement().setAttribute("tabindex", "-1");
            nameDiv.getStyle()
                    .set("margin", "2px")
                    .set("border", "1px solid var(--lumo-contrast-20pct)")
                    .set("border-radius", "var(--lumo-border-radius-m)")
                    .set("padding", "var(--lumo-space-s)")
                    .set("background-color", "var(--lumo-base-color)")
                    .set("height", "var(--lumo-text-field-size)")
                    .set("box-shadow", "var(--lumo-box-shadow-xs)")
                    .set("font-size", "var(--lumo-font-size-m)")
                    .set("line-height", "var(--lumo-line-height-m)")
                    .set("color", "var(--lumo-body-text-color)")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("padding-left", "var(--lumo-space-m)")
                    .set("padding-right", "var(--lumo-space-m)")
                    .set("justify-content", "center")
                    .set("overflow-x", "clip");

            descriptionDiv = new Div();
            descriptionDiv.setText(orderData.getVariant().getDescription());
            descriptionDiv.getElement().setAttribute("tabindex", "-1");
            descriptionDiv.getStyle()
                    .set("margin", "2px")
                    .set("border", "1px solid var(--lumo-contrast-20pct)")
                    .set("border-radius", "var(--lumo-border-radius-m)")
                    .set("padding", "var(--lumo-space-s)")
                    .set("background-color", "var(--lumo-base-color)")
                    .set("height", "15em")
                    .set("overflow", "auto")
                    .set("white-space", "pre-wrap")
                    .set("box-shadow", "var(--lumo-box-shadow-xs)")
                    .set("font-size", "var(--lumo-font-size-m)")
                    .set("line-height", "var(--lumo-line-height-m)")
                    .set("color", "var(--lumo-body-text-color)")
                    .set("text-align", "center")
                    .set("padding-left", "var(--lumo-space-m)")
                    .set("padding-right", "var(--lumo-space-m)");

            amountField = new NumberField();
            amountField.setAllowedCharPattern("[0-9]");
            amountField.setMin(0);
            amountField.setMax(Integer.MAX_VALUE);
            amountField.setValue(Integer.valueOf(orderData.getAmount()).doubleValue());
            amountField.setWidthFull();

            amountBinder = new Binder<>();
            amountBinder.forField(amountField)
                    .asRequired()
                    .bind(NumberField::getValue, NumberField::setValue);

            this.add(nameDiv, descriptionDiv, amountField);
            this.setWidth("calc(100em/3)");
            this.getStyle()
                    .set("border", "1px solid var(--lumo-contrast-20pct)")
                    .set("border-radius", "var(--lumo-border-radius-m)")
                    .set("padding", "var(--lumo-space-s)")
                    .set("transform", "scale(1)");
        }

        public boolean validate()
        {
            amountBinder.validate();

            return amountBinder.isValid();
        }

        public void save()
        {

            orderData.getId().setOrderId(order.getId());
            orderData.setAmount(amountField.getValue().intValue());

            orderManager.getOrderDataService().update(orderData);
        }

    }
}
