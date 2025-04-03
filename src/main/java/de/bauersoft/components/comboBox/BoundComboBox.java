package de.bauersoft.components.comboBox;

import com.vaadin.flow.component.combobox.ComboBox;

import java.util.Collection;
import java.util.Objects;

public class BoundComboBox<B, T> extends ComboBox<T>
{
    private B bound;



    public BoundComboBox(int pageSize, B bound)
    {
        super(pageSize);
        this.bound = bound;
    }

    public BoundComboBox(B bound)
    {
        this.bound = bound;
    }

    public BoundComboBox(String label, B bound)
    {
        super(label);
        this.bound = bound;
    }

    public BoundComboBox(String label, Collection<T> items, B bound)
    {
        super(label, items);
        this.bound = bound;
    }

    public BoundComboBox(String label, B bound, T... items)
    {
        super(label, items);
        this.bound = bound;
    }

    public BoundComboBox(ValueChangeListener<ComponentValueChangeEvent<ComboBox<T>, T>> listener, B bound)
    {
        super(listener);
        this.bound = bound;
    }

    public BoundComboBox(String label, ValueChangeListener<ComponentValueChangeEvent<ComboBox<T>, T>> listener, B bound)
    {
        super(label, listener);
        this.bound = bound;
    }

    public BoundComboBox(String label, ValueChangeListener<ComponentValueChangeEvent<ComboBox<T>, T>> listener, B bound, T... items)
    {
        super(label, listener, items);
        this.bound = bound;
    }

    public B getBound()
    {
        return bound;
    }

    public BoundComboBox<B, T> setBound(B bound)
    {
        Objects.requireNonNull(bound);

        this.bound = bound;
        return this;
    }
}
