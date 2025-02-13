package de.bauersoft.views.order.institutionLayer.fieldLayer;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.views.order.OrderManager;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calendarLayer.CalendarCluster;
import lombok.Getter;

@Getter
public class FieldTab extends Div
{
    private final OrderManager orderManager;
    private final FieldTabSheet fieldTabSheet;
    private final InstitutionField institutionField;

    private final Tab tab;

    private CalendarCluster calendarCluster;

    public FieldTab(OrderManager orderManager, FieldTabSheet fieldTabSheet, InstitutionField institutionField)
    {
        this.orderManager = orderManager;
        this.fieldTabSheet = fieldTabSheet;
        this.institutionField = institutionField;

        tab = new Tab(institutionField.getField().getName());

        calendarCluster = new CalendarCluster(orderManager, this);

        this.add(calendarCluster);
    }
}
