package de.bauersoft.views.order.institutionLayer.fieldLayer;

import com.vaadin.flow.component.tabs.TabSheet;
import de.bauersoft.data.entities.institution.InstitutionField;
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

    private final Map<InstitutionField, FieldTab> fieldTabMap;

    public FieldTabSheet(OrderManager orderManager, InstitutionTab institutionTab)
    {
        this.orderManager = orderManager;
        this.institutionTab = institutionTab;

        fieldTabMap = new HashMap<>();

        for(InstitutionField institutionField : institutionTab.getInstitution().getInstitutionFields())
        {
            FieldTab fieldTab = new FieldTab(orderManager, this, institutionField);
            fieldTabMap.put(institutionField, fieldTab);

            this.add(fieldTab.getTab(), fieldTab);
        }

        this.setWidthFull();
        this.setHeightFull();
    }
}
