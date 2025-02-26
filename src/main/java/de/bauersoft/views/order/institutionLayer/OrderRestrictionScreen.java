package de.bauersoft.views.order.institutionLayer;

import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.Style;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.views.order.OrderManager;

public class OrderRestrictionScreen extends Div
{
    private final OrderManager orderManager;
    private final Institution institution;

    public OrderRestrictionScreen(OrderManager orderManager, Institution institution)
    {
        this.orderManager = orderManager;
        this.institution = institution;

        this.setWidthFull();
        this.setHeightFull();

        this.getStyle()
                .setBackgroundColor("rgba(25, 51, 130, 1)")
                .setDisplay(Style.Display.FLEX)
                .setFlexDirection(Style.FlexDirection.COLUMN)
                .setAlignItems(Style.AlignItems.CENTER)
                .setJustifyContent(Style.JustifyContent.CENTER)
                .setBorderRadius("var(--lumo-border-radius-m)");

        Icon lockIcon = new Icon(VaadinIcon.LOCK);
        lockIcon.setSize("5em");
        lockIcon.setColor("white");

        Span smallSpan = new Span("Bei Fragen wenden Sie sich an Rettel.");
        smallSpan.getStyle().setFontSize("0.5em");

        Span lockMessage = new Span();
        lockMessage.add(
                new Span("Bestellungen für " + institution.getName() + " sind geschlossen."),
                new HtmlComponent("br"),
                new Span("Bestellzeitraum: " + institution.getOrderStart() + " - " + institution.getOrderEnd() + " Uhr"),
                new HtmlComponent("br"),
                smallSpan);
        lockMessage.getStyle()
                .set("color", "white")
                .set("font-size", "2em")
                .set("margin-top", "1em")
                .setTextAlign(Style.TextAlign.CENTER);

        this.add(lockIcon, lockMessage);
    }
}
