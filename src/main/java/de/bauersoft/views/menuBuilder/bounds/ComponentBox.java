package de.bauersoft.views.menuBuilder.bounds;

import com.vaadin.flow.component.combobox.ComboBox;
import de.bauersoft.data.entities.Component;
import de.bauersoft.data.entities.Course;
import de.bauersoft.data.entities.pattern.DefaultPattern;

import java.util.Collection;

public class ComponentBox extends ComboBox<Component>
{
    private Course bound;
    private DefaultPattern matcher;
    private int sectionIndex;

    public ComponentBox(int pageSize)
    {
        super(pageSize);
    }

    public ComponentBox()
    {
    }

    public ComponentBox(String label)
    {
        super(label);
    }

    public ComponentBox(String label, Collection<Component> items)
    {
        super(label, items);
    }

    public ComponentBox(String label, Component... items)
    {
        super(label, items);
    }

    public ComponentBox(ValueChangeListener<ComponentValueChangeEvent<ComboBox<Component>, Component>> listener)
    {
        super(listener);
    }

    public ComponentBox(String label, ValueChangeListener<ComponentValueChangeEvent<ComboBox<Component>, Component>> listener)
    {
        super(label, listener);
    }

    public ComponentBox(String label, ValueChangeListener<ComponentValueChangeEvent<ComboBox<Component>, Component>> listener, Component... items)
    {
        super(label, listener, items);
    }

    public Course getBound()
    {
        return bound;
    }

    public ComponentBox setBound(Course bound)
    {
        this.bound = bound;
        return this;
    }

    public DefaultPattern getMatcher()
    {
        return matcher;
    }

    public ComponentBox setMatcher(DefaultPattern matcher)
    {
        this.matcher = matcher;
        return this;
    }

    public int getSectionIndex()
    {
        return sectionIndex;
    }

    public ComponentBox setSectionIndex(int sectionIndex)
    {

        this.sectionIndex = sectionIndex;
        return this;
    }
}
