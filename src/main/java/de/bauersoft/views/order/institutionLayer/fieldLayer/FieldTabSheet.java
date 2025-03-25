package de.bauersoft.views.order.institutionLayer.fieldLayer;

import com.vaadin.flow.component.tabs.TabSheet;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.views.order.OrderManager;
import de.bauersoft.views.order.institutionLayer.InstitutionTab;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class FieldTabSheet extends TabSheet
{
    private final OrderManager orderManager;
    private final InstitutionTab institutionTab;

    private final Map<InstitutionField, FieldTab> fieldFieldTabMap;

    public FieldTabSheet(OrderManager orderManager, InstitutionTab institutionTab)
    {
        this.orderManager = orderManager;
        this.institutionTab = institutionTab;

        this.fieldFieldTabMap = new HashMap<>();

        for(InstitutionField institutionField : institutionTab.getInstitution().getInstitutionFields())
        {
            FieldTab fieldTab = new FieldTab(orderManager, this, institutionField);
            fieldFieldTabMap.put(institutionField, fieldTab);

            this.add(fieldTab.getTab(), fieldTab);
        }

        this.setWidthFull();
        this.setHeightFull();
    }
}
