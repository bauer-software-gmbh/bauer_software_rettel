package de.bauersoft.views.menuBuilderNew.cluster;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class VerticalCluster extends VerticalLayout
{
    public VerticalCluster()
    {
        this.setPadding(false);
        this.setSpacing(false);
        this.setAlignItems(Alignment.STRETCH);
        this.setWidth("10rem");
    }
}
