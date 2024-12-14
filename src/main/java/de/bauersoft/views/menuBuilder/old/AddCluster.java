package de.bauersoft.views.menuBuilder.old;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import de.bauersoft.views.menuBuilder.cluster.ClusterLayout;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.Objects;


public class AddCluster extends ClusterLayout
{

    private Button addButton;
    private ComponentEventListener<ClickEvent<Button>> addButtonListener;

    public AddCluster()
    {
        addButton = new Button();
        addButton.setIcon(LineAwesomeIcon.PLUS_SQUARE.create());

        this.addComponents(getPlaceholder(), addButton);
        updateComponents();
    }

    public Button getAddButton()
    {
        return addButton;
    }

    public AddCluster setAddButton(Button addButton)
    {
        this.addButton = addButton;
        return this;
    }



    public ComponentEventListener<ClickEvent<Button>> getAddButtonListener()
    {
        return addButtonListener;
    }

    public AddCluster setAddButtonListener(ComponentEventListener<ClickEvent<Button>> addButtonListener)
    {
        Objects.requireNonNull(addButtonListener, "addButtonListener cannot be null");

        this.addButtonListener = addButtonListener;
        addButton.addClickListener(addButtonListener);
        return this;
    }
}
