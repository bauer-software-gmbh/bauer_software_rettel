package de.bauersoft.views.order.institutionLayer.fieldLayer;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.views.order.OrderManager;
import de.bauersoft.views.order.institutionLayer.InstitutionTab;
import de.bauersoft.views.order.institutionLayer.InstitutionTabSheet;
import de.bauersoft.views.order.institutionLayer.fieldLayer.calendarLayer.CalendarCluster;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
