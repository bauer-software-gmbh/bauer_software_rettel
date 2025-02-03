package de.bauersoft.views.menuBuilder.cluster;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.pattern.Pattern;

import java.util.*;
import java.util.stream.Collectors;

public class ClusterLayout extends VerticalLayout
{

    private List<? extends Component> components;

    public ClusterLayout() {
        components = new ArrayList<>();

        this.setPadding(false);
        this.setSpacing(false);
        this.setWidth("200px");
        this.setAlignItems(Alignment.STRETCH);
    }

    public <E extends Component> ClusterLayout addComponent(E component)
    {
        addToList(components, component);
        return this;
    }

    public <E extends Component> ClusterLayout addComponent(E component, int index)
    {
        addAtIndex((List<E>) components, index, component);
        return this;
    }

    public <E extends Component> ClusterLayout addComponents(Collection<? extends E> newComponents)
    {
        ((List<E>) components).addAll(newComponents);
        return this;
    }

    public <E extends Component> ClusterLayout addComponents(Collection<? extends E> newComponents, int index)
    {
        addAllAtIndex((List<E>) components, index, newComponents);
        return this;
    }

    public <E extends Component> ClusterLayout addComponents(E... componentsToAdd)
    {
        for (E component : componentsToAdd) {
            addToList(components, component);
        }
        return this;
    }

    public <E extends Component> ClusterLayout removeComponent(E component)
    {
        ((List<E>) components).remove(component);
        return this;
    }

    public <E extends Component> ClusterLayout removeComponents(Collection<? extends E> componentsToRemove)
    {
        ((List<E>) components).removeAll(componentsToRemove);
        return this;
    }

    public <E extends Component> ClusterLayout removeComponents(E... componentsToRemove)
    {
        for (E component : componentsToRemove) {
            ((List<E>) components).remove(component);
        }
        return this;
    }

    public <E extends Component> ClusterLayout removeComponents(int startIndex, int endIndex)
    {
        removeRange((List<E>) components, startIndex, endIndex);
        return this;
    }

    public List<? extends Component> getComponents()
    {
        return components;
    }

    public <E extends Component> ClusterLayout setComponents(List<? extends E> components)
    {
        this.components = components;
        return this;
    }

    public ClusterLayout updateComponents()
    {
        this.removeAll();
        this.add((Collection<Component>) components);

        return this;
    }



    public TextField getPlaceholder()
    {
        TextField placeholder = new TextField();
        placeholder.setReadOnly(true);

        return placeholder;
    }

    public static <E> void addAtIndex(List<E> list, int index, E item)
    {
        if(index < 0)
        {
            list.add(0, item);

        }else if (index >= list.size())
        {list.add(item);

        }else list.add(index, item);
    }

    public static <E> void addAllAtIndex(List<E> list, int index, Collection<? extends E> items)
    {
        if(index < 0)
        {
            list.addAll(0, items);

        }else if (index >= list.size())
        {
            list.addAll(items);

        }else list.addAll(index, items);
    }

    public static <E> void removeRange(List<E> list, int startIndex, int endIndex)
    {
        if(startIndex < 0)
            startIndex = 0;

        if(endIndex >= list.size())
            endIndex = list.size() - 1;

        if(startIndex <= endIndex)
            list.subList(startIndex, endIndex + 1).clear();
    }



    public static Collection<de.bauersoft.data.entities.component.Component> getCourseMatchingComponents(Collection<de.bauersoft.data.entities.component.Component> components, Course toMatch)
    {
        Objects.requireNonNull(components, "components cannot be null");
        if(components.contains(null))
            throw new NullPointerException("components contains null");

        if(toMatch == null) return components;

        Collection<de.bauersoft.data.entities.component.Component> matching = components.stream()
                .filter(component -> component.getCourse().equals(toMatch))
                .collect(Collectors.toList());

        return matching;
    }

    public static Collection<de.bauersoft.data.entities.component.Component> getPatternMatchingComponents(Collection<de.bauersoft.data.entities.component.Component> components, Pattern toMatch)
    {
        Objects.requireNonNull(components, "components cannot be null");
        if(components.contains(null))
            throw new NullPointerException("components contains null");

        if(toMatch == null) return components;

        Collection<de.bauersoft.data.entities.component.Component> matching = components.stream()
                .filter(component -> component.getRecipes().stream()
                        .allMatch(recipe -> recipe.getPatterns().stream()
                                .anyMatch(pattern -> pattern.equals(toMatch))))
                .collect(Collectors.toList());

        return matching;
    }

    public static List<Pattern> getMatchingPatternsForComponent(de.bauersoft.data.entities.component.Component component)
    {
        Objects.requireNonNull(component, "component cannot be null");

        if(component.getRecipes() == null)
            throw new NullPointerException("component's recipes cannot be null");

        if(component.getRecipes().isEmpty())
            return Collections.emptyList();

        return component.getRecipes().stream()
                .map(recipe -> recipe.getPatterns())
                .reduce((patterns1, patterns2) ->
                {
                    patterns1.retainAll(patterns2);
                    return patterns1;

                }).map(ArrayList::new)
                .orElse(new ArrayList<>());
    }


    private <E extends Component> void addToList(Collection<? extends Component> components, E item)
    {
        ((Collection<E>) components).add(item);
    }

}
