package de.bauersoft.views.closingTime;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.bauersoft.services.InstitutionClosingTimeService;
import org.vaadin.lineawesome.LineAwesomeIcon;

public class ClosingTimeComponent extends VerticalLayout
{

    private final InstitutionClosingTimeService closingTimeService;
    private final Button addButton;

    public ClosingTimeComponent(InstitutionClosingTimeService closingTimeService)
    {
        this.closingTimeService = closingTimeService;

        addButton = new Button("Schließzeitraum hinzufügen", LineAwesomeIcon.PLUS_SOLID.create());
        addButton.addClickListener(event ->
        {

        });

        this.add(addButton);
    }

    private class ClosingTimeRow
    {

    }
}
