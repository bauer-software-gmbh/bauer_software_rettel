package de.bauersoft.views.order.institutionLayer;

import com.vaadin.flow.component.tabs.TabSheet;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.views.order.OrderManager;
import de.bauersoft.views.order.institutionLayer.fieldLayer.FieldTabSheet;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class InstitutionTabSheet extends TabSheet
{
    private OrderManager orderManager;

    private Map<Institution, FieldTabSheet> fieldTabSheetMap;

    public InstitutionTabSheet(OrderManager orderManager)
    {
        Objects.requireNonNull(orderManager, "OrderManager cannot be null!");

        this.orderManager = orderManager;
        fieldTabSheetMap = new HashMap<>();

        this.setWidthFull();
        this.setHeightFull();

        for(Institution institution : orderManager.getInstitutionService().findAllByUsersId(orderManager.getUser().getId()))
        {
            FieldTabSheet fieldTabSheet = new FieldTabSheet(orderManager, this, institution);
            //fieldTabSheet.setVisible(institution.getInstitutionFields().size() > 0);

            fieldTabSheetMap.put(institution, fieldTabSheet);
            this.add(institution.getName(), fieldTabSheet);
        }

    }

    public boolean validate()
    {
        return fieldTabSheetMap.values().stream().allMatch(FieldTabSheet::validate);
    }

    public void save()
    {
        fieldTabSheetMap.values().forEach(FieldTabSheet::save);
    }

}
