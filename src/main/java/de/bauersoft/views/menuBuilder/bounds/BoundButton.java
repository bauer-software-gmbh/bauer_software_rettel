package de.bauersoft.views.menuBuilder.bounds;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;

public class BoundButton<B> extends Button
{
    private B bound;

    public BoundButton()
    {
        super();
    }

    public BoundButton(B bound)
    {
        this.bound = bound;
    }

    public BoundButton(String text, B bound)
    {
        super(text);
        this.bound = bound;
    }

    public BoundButton(Component icon, B bound)
    {
        super(icon);
        this.bound = bound;
    }

    public BoundButton(String text, Component icon, B bound)
    {
        super(text, icon);
        this.bound = bound;
    }

    public BoundButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener, B bound)
    {
        super(text, clickListener);
        this.bound = bound;
    }

    public BoundButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener, B bound)
    {
        super(icon, clickListener);
        this.bound = bound;
    }

    public BoundButton(String text, Component icon, ComponentEventListener<ClickEvent<Button>> clickListener, B bound)
    {
        super(text, icon, clickListener);
        this.bound = bound;
    }

    public B getBound()
    {
        return bound;
    }

    public void setBound(B bound)
    {
        this.bound = bound;
    }
}
