package de.bauersoft.views.order.institutionLayer;

import com.vaadin.flow.component.tabs.TabSheet;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.role.Role;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.services.InstitutionService;
import de.bauersoft.views.order.OrderManager;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class InstitutionTabSheet extends TabSheet
{
    private final OrderManager orderManager;

    private final InstitutionService institutionService;

    private final User user;

    private final Map<Institution, InstitutionTab> institutionTabMap;

    public InstitutionTabSheet(OrderManager orderManager)
    {
        this.orderManager = orderManager;

        institutionService = orderManager.getInstitutionService();

        user = orderManager.getUser();

        institutionTabMap = new HashMap<>();

        if(user.getRoles().contains(Role.ORDER_SHOW_ALL_INSTITUTIONS) || user.getRoles().contains(Role.ADMIN))
        {
            for(Institution institution : institutionService.findAll())
            {
                InstitutionTab institutionTab = new InstitutionTab(orderManager, this, institution);
                institutionTabMap.put(institution, institutionTab);

                this.add(institutionTab.getTab(), institutionTab);
            }

        }else
        {
            for(Institution institution : institutionService.findAllByUsersId(user.getId()))
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
