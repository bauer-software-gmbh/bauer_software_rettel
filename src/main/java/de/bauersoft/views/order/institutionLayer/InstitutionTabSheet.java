package de.bauersoft.views.order.institutionLayer;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.views.order.OrderManager;
import de.bauersoft.views.order.institutionLayer.fieldLayer.FieldTabSheet;
import lombok.Getter;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class InstitutionTabSheet extends TabSheet
{
    private final OrderManager orderManager;

    private final Map<Institution, InstitutionTab> institutionTabMap;

    public InstitutionTabSheet(OrderManager orderManager)
    {
        Objects.requireNonNull(orderManager, "OrderManager cannot be null!");

        this.orderManager = orderManager;

        institutionTabMap = new HashMap<>();

        for(Institution institution : orderManager.getInstitutionService().findAllByUsersId(orderManager.getUser().getId()))
        {
            InstitutionTab institutionTab = new InstitutionTab(orderManager, this, institution);
            institutionTabMap.put(institution, institutionTab);

            this.add(institutionTab.getTab(), institutionTab);
        }

        this.setWidthFull();
        this.setHeightFull();
    }

}
