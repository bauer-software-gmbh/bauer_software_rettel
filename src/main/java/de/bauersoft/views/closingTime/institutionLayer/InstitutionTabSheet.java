package de.bauersoft.views.closingTime.institutionLayer;

import com.vaadin.flow.component.tabs.TabSheet;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.role.Role;
import de.bauersoft.views.closingTime.ClosingTimeManager;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class InstitutionTabSheet extends TabSheet
{
    private final ClosingTimeManager closingTimeManager;

    private final Map<Institution, InstitutionTab> institutionTabMap;

    public InstitutionTabSheet(ClosingTimeManager closingTimeManager)
    {
        this.closingTimeManager =  closingTimeManager;

        institutionTabMap = new HashMap<>();

        if(closingTimeManager.getUser().getRoles().contains(Role.CLOSING_TIMES_SHOW_ALL_INSTITUTIONS))
        {
            for(Institution institution : closingTimeManager.getInstitutionService().findAll())
            {
                InstitutionTab institutionTab = new InstitutionTab(closingTimeManager, this, institution);
                institutionTabMap.put(institution, institutionTab);

                this.add(institutionTab.getTab(), institutionTab);
            }

        }else
        {
            for(Institution institution : closingTimeManager.getInstitutionService().findAllByUsersId(closingTimeManager.getUser().getId()))
            {
                InstitutionTab institutionTab = new InstitutionTab(closingTimeManager, this, institution);
                institutionTabMap.put(institution, institutionTab);

                this.add(institutionTab.getTab(), institutionTab);
            }
        }

        this.setWidthFull();
        this.setHeightFull();
    }
}
