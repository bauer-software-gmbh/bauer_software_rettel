package de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer.orderLayer.variantLayer;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.theme.lumo.LumoUtility;
import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.order.Order;
import de.bauersoft.data.entities.order.OrderData;
import de.bauersoft.data.entities.order.OrderDataKey;
import de.bauersoft.data.entities.variant.Variant;
import de.bauersoft.views.order.OrderManager;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer.orderLayer.OrderComponent;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class VariantComponent extends HorizontalLayout
{
    private final OrderManager orderManager;
    private final OrderComponent orderComponent;
    private final Menu menu;
    private final Order order;

    private final InstitutionField institutionField;

    private final VariantMapContainer variantMapContainer;

    private final Map<Variant, VariantBox> variantBoxMap;

    public VariantComponent(OrderManager orderManager,OrderComponent orderComponent, Menu menu, Order order, VariantMapContainer variantMapContainer)
    {
        this.orderManager = orderManager;
        this.orderComponent = orderComponent;
        this.menu = menu;
        this.order = order;

        institutionField = orderComponent.getInstitutionField();

        this.variantMapContainer = variantMapContainer;

        this.variantBoxMap = new HashMap<>();

        for(Variant variant : menu.getVariants())
        {
            VariantContainer variantContainer = (VariantContainer)  variantMapContainer.addIfAbsent(variant, () ->
            {
                OrderData orderData = new OrderData();
                orderData.setId(new OrderDataKey(null, variant.getId()));
                orderData.setVariant(variant);
                orderData.set_order(order);

                return orderData;

            }, ContainerState.SHOW);

            VariantBox variantBox = new VariantBox(variantContainer);
            variantBoxMap.put(variant, variantBox);

            this.add(variantBox);
        }

        this.setWidthFull();
        this.getStyle()
                .setFlexWrap(Style.FlexWrap.WRAP)
                .setJustifyContent(Style.JustifyContent.FLEX_START)
                .set("gap", "var(--lumo-space-m)");
    }

    private class VariantBox extends Div
    {
        private final Div nameDiv;
        private final Div descriptionDiv;
        private final NumberField amountField;

        private final VariantContainer variantContainer;

        public VariantBox(VariantContainer variantContainer)
        {
            this.variantContainer = variantContainer;

            nameDiv = new Div();
            nameDiv.setText(variantContainer.getEntity().getVariant().getPattern().getName());
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
            descriptionDiv.setText(variantContainer.getEntity().getVariant().getDescription());
            descriptionDiv.getElement().setAttribute("tabindex", "-1");
            descriptionDiv.getStyle()
                    .set("margin", "2px")
                    .set("border", "1px solid var(--lumo-contrast-20pct)")
                    .set("border-radius", "var(--lumo-border-radius-m)")
                    .set("padding", "var(--lumo-space-s)")
                    .set("background-color", "var(--lumo-base-color)")
                    .set("height", "10em")
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
            amountField.setWidthFull();
            amountField.setAllowedCharPattern("[0-9]");
            amountField.setMin(0);
            amountField.setMax(Integer.MAX_VALUE);

            amountField.setValue(variantContainer.getEntity().getAmount().doubleValue());

            amountField.addValueChangeListener(event ->
            {
                variantContainer.setTempState(ContainerState.UPDATE);
                variantContainer.setTempAmount(Objects.requireNonNullElse(event.getValue(), 0).intValue());
            });

            this.add(nameDiv, descriptionDiv, amountField);
//            this.setWidth("calc(100em/3)");
//            this.getStyle()
//                    .set("border", "1px solid var(--lumo-contrast-20pct)")
//                    .set("border-radius", "var(--lumo-border-radius-m)")
//                    .set("padding", "var(--lumo-space-s)")
//                    .set("transform", "scale(1)");

            //this.setMaxWidth("var(--lumo-size-m)");
            this.getStyle()
                    .setWidth("calc(100em/5)")
                    .setMaxWidth("calc(100em/5)");
        }
    }
}
