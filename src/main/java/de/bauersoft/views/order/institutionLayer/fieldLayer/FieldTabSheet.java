package de.bauersoft.views.order.institutionLayer.fieldLayer;

import com.vaadin.flow.component.tabs.TabSheet;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.views.order.OrderManager;
import de.bauersoft.views.order.institutionLayer.InstitutionTabSheet;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calendarLayer.CalendarCluster;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class FieldTabSheet extends TabSheet
{
    private final OrderManager orderManager;
    private final InstitutionTabSheet institutionTabSheet;
    private final Institution institution;

    private final Map<Field, CalendarCluster> calendarClusterMap;

    public FieldTabSheet(OrderManager orderManager, InstitutionTabSheet institutionTabSheet, Institution institution)
    {
        Objects.requireNonNull(orderManager, "OrderManager cannot be null!");
        Objects.requireNonNull(institutionTabSheet, "InstitutionTabSheet cannot be null!");
        Objects.requireNonNull(institution, "Institution cannot be null!");

        this.orderManager = orderManager;
        this.institutionTabSheet = institutionTabSheet;
        this.institution = institution;

        calendarClusterMap = new HashMap<>();

        this.setWidthFull();
        this.setHeightFull();

        for(InstitutionField institutionField : institution.getInstitutionFields())
        {
            CalendarCluster calendarCluster = new CalendarCluster(orderManager, this, institutionField);

            calendarClusterMap.put(institutionField.getField(), calendarCluster);
            this.add(institutionField.getField().getName(), calendarCluster);
        }
    }

    public boolean validate()
    {
        return calendarClusterMap.values().stream().allMatch(CalendarCluster::validate);
    }

    public void save()
    {
        calendarClusterMap.values().forEach(CalendarCluster::save);
    }
}
