package de.bauersoft.views.order.institutionLayer;

import com.vaadin.flow.component.tabs.TabSheet;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.role.Role;
import de.bauersoft.views.order.OrderManager;
import lombok.Getter;

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

        if(orderManager.getUser().getRoles().contains(Role.ORDER_SHOW_ALL_INSTITUTIONS))
        {
            for(Institution institution : orderManager.getInstitutionService().findAll())
            {
                InstitutionTab institutionTab = new InstitutionTab(orderManager, this, institution);
                institutionTabMap.put(institution, institutionTab);

                this.add(institutionTab.getTab(), institutionTab);
            }

        }else
        {
            for(Institution institution : orderManager.getInstitutionService().findAllByUsersId(orderManager.getUser().getId()))
            {
                InstitutionTab institutionTab = new InstitutionTab(orderManager, this, institution);
                institutionTabMap.put(institution, institutionTab);

                this.add(institutionTab.getTab(), institutionTab);
            }
        }

        this.setWidthFull();
        this.setHeightFull();
    }

}
