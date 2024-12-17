package de.bauersoft.views.menuBuilder.bounds;

import com.vaadin.flow.component.textfield.TextField;

public class BoundTextField<B> extends TextField
{
    private B bound;

    public BoundTextField()
    {
        super();
    }

    public BoundTextField(B bound)
    {
        this.bound = bound;
    }

    public BoundTextField(String label, B bound)
    {
        super(label);
        this.bound = bound;
    }

    public BoundTextField(String label, String placeholder, B bound)
    {
        super(label, placeholder);
        this.bound = bound;
    }

    public BoundTextField(String label, String initialValue, String placeholder, B bound)
    {
        super(label, initialValue, placeholder);
        this.bound = bound;
    }

    public BoundTextField(ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener, B bound)
    {
        super(listener);
        this.bound = bound;
    }

    public BoundTextField(String label, ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener, B bound)
    {
        super(label, listener);
        this.bound = bound;
    }

    public BoundTextField(String label, String initialValue, ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener, B bound)
    {
        super(label, initialValue, listener);
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
