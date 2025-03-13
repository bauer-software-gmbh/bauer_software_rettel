package de.bauersoft.views.order.institutionLayer.fieldLayer;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.views.order.OrderManager;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calenderLayer.CalenderComponent;
import lombok.Getter;

@Getter
public class FieldTab extends Div
{
    private final OrderManager orderManager;
    private final FieldTabSheet fieldTabSheet;
    private final InstitutionField institutionField;

    private final Tab tab;

    private final CalenderComponent calenderComponent;

    public FieldTab(OrderManager orderManager, FieldTabSheet fieldTabSheet, InstitutionField institutionField)
    {
        this.orderManager = orderManager;
        this.fieldTabSheet = fieldTabSheet;
        this.institutionField = institutionField;

        tab = new Tab(institutionField.getField().getName());

        calenderComponent = new CalenderComponent(orderManager, this);
        this.add(calenderComponent);
    }
}

