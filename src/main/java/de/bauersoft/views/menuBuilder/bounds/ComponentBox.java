package de.bauersoft.views.menuBuilder.bounds;

import com.vaadin.flow.component.combobox.ComboBox;
import de.bauersoft.data.entities.component.Component;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.pattern.DefaultPattern;

import java.util.Collection;

public class ComponentBox extends BoundComboBox<Course, Component>
{
    private DefaultPattern matcher;

    public ComponentBox(int pageSize, Course bound)
    {
        super(pageSize, bound);
    }

    public ComponentBox(Course bound)
    {
        super(bound);
    }

    public ComponentBox(String label, Course bound)
    {
        super(label, bound);
    }

    public ComponentBox(String label, Collection<Component> items, Course bound)
    {
        super(label, items, bound);
    }

    public ComponentBox(String label, Course bound, Component... items)
    {
        super(label, bound, items);
    }

    public ComponentBox(ValueChangeListener<ComponentValueChangeEvent<ComboBox<Component>, Component>> listener, Course bound)
    {
        super(listener, bound);
    }

    public ComponentBox(String label, ValueChangeListener<ComponentValueChangeEvent<ComboBox<Component>, Component>> listener, Course bound)
    {
        super(label, listener, bound);
    }

    public ComponentBox(String label, ValueChangeListener<ComponentValueChangeEvent<ComboBox<Component>, Component>> listener, Course bound, Component... items)
    {
        super(label, listener, bound, items);
    }

    public DefaultPattern getMatcher()
    {
        return matcher;
    }

    public void setMatcher(DefaultPattern matcher)
    {
        this.matcher = matcher;
    }
}
