package de.bauersoft.views.order.institutionLayer;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.role.Role;
import de.bauersoft.views.order.OrderManager;
import de.bauersoft.views.order.institutionLayer.fieldLayer.FieldTabSheet;
import lombok.Getter;

import java.time.*;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Getter
public class InstitutionTab extends Div
{
    private final OrderManager orderManager;
    private final InstitutionTabSheet institutionTabSheet;
    private final Institution institution;

    public final Tab tab;

    private Timer scheduler;

    public FieldTabSheet fieldTabSheet;
    public OrderRestrictionScreen orderRestrictionScreen;

    public InstitutionTab(OrderManager orderManager, InstitutionTabSheet institutionTabSheet, Institution institution)
    {
        this.orderManager = orderManager;
        this.institutionTabSheet = institutionTabSheet;

        this.institution = institution;

        tab = new Tab(institution.getName());

        fieldTabSheet = new FieldTabSheet(orderManager, this);
        this.add(fieldTabSheet);

       if(!orderManager.getUser().getRoles().contains(Role.ADMIN) &&
               !orderManager.getUser().getRoles().contains(Role.ORDER_TIME_BYPASS))
       {
           LocalDateTime orderEndDateTime = LocalDateTime.of(LocalDate.now(), institution.getOrderEnd());
           Date orderEndDate = Date.from(orderEndDateTime.atZone(ZoneId.systemDefault()).toInstant());

           UI currentUI = UI.getCurrent();

           scheduler = new Timer();
           scheduler.schedule(new TimerTask()
           {
               @Override
               public void run()
               {
                   currentUI.access(() ->
                   {
                       orderRestrictionScreen = new OrderRestrictionScreen(orderManager, institution);
                       if(fieldTabSheet != null)
                           InstitutionTab.this.remove(fieldTabSheet);

                       InstitutionTab.this.add(orderRestrictionScreen);
                   });
               }
           }, orderEndDate);
       }

        this.setWidthFull();
        this.setHeightFull();
    }
}
