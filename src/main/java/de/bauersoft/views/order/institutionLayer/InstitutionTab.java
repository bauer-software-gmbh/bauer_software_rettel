package de.bauersoft.views.order.institutionLayer;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.role.Role;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.views.order.OrderManager;
import de.bauersoft.views.order.institutionLayer.fieldLayer.FieldTabSheet;
import lombok.Getter;
import org.aspectj.weaver.ast.Or;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Getter
public class InstitutionTab extends Div
{
    private final OrderManager orderManager;
    private final InstitutionTabSheet institutionTabSheet;
    private final Institution institution;

    private final User user;

    private final Tab tab;

    private Timer scheduler;

    private final FieldTabSheet fieldTabSheet;
    private OrderRestrictionScreen orderRestrictionScreen;

    public InstitutionTab(OrderManager orderManager, InstitutionTabSheet institutionTabSheet, Institution institution)
    {
        this.orderManager = orderManager;
        this.institutionTabSheet = institutionTabSheet;
        this.institution = institution;

        user = orderManager.getUser();

        tab = new Tab(institution.getName());

        this.setWidthFull();
        this.setHeightFull();

        fieldTabSheet = new FieldTabSheet(orderManager, this);
        this.add(fieldTabSheet);

        if(user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.ORDER_TIME_BYPASS)) return;

        LocalDateTime orderEndDateTime = LocalDateTime.of(LocalDate.now(), institution.getOrderEnd());
        Date orderEndDate = Date.from(orderEndDateTime.atZone(ZoneId.systemDefault()).toInstant());

        UI ui = UI.getCurrent();

        scheduler = new Timer(true);;
        scheduler.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                ui.access(() ->
                {
                    orderRestrictionScreen = new OrderRestrictionScreen(orderManager, institution);

                    if(fieldTabSheet != null)
                        InstitutionTab.this.remove(fieldTabSheet);

                    InstitutionTab.this.add(orderRestrictionScreen);
                });
            }
        }, orderEndDate);
    }
}
