package de.bauersoft.views.menuBuilder.bounds;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import de.bauersoft.data.entities.Component;
import de.bauersoft.data.entities.Course;
import de.bauersoft.data.entities.pattern.DefaultPattern;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.*;
import java.util.stream.Collectors;

public class RowCluster extends FlexLayout
{
    private Course bound;
    private TextField boundField;

    private Button addButton;
    private ComponentEventListener<ClickEvent<Button>> addButtonListener;

    private Button removeButton;
    private ComponentEventListener<ClickEvent<Button>> removeButtonListener;

    private List<ComponentBox> componentBoxes;

    public RowCluster(Course bound)
    {
        Objects.requireNonNull(bound);

        this.bound = bound;

        boundField = new TextField();
        boundField.setValue(bound.getName());
        boundField.getElement().getStyle().set("margin", "5px");
        boundField.setWidth("calc(20% - 10px)");
        boundField.setReadOnly(true);
        boundField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);

        addButton = new Button();
        addButton.setIcon(LineAwesomeIcon.PLUS_SQUARE.create());
        addButton.getElement().getStyle().set("margin", "5px");

        removeButton = new Button();
        removeButton.setIcon(LineAwesomeIcon.MINUS_SQUARE.create());
        removeButton.getElement().getStyle().set("margin", "5px");

        componentBoxes = new ArrayList<>();

        updateCluster();
    }

    public Course getBound()
    {
        return bound;
    }

    public RowCluster setBound(Course bound)
    {
        Objects.requireNonNull(bound, "bound cannot be null");

        this.bound = bound;
        return this;
    }

    public TextField getBoundField()
    {
        return boundField;
    }

    public RowCluster setBoundField(TextField boundField)
    {
        Objects.requireNonNull(boundField, "boundField cannot be null");

        this.boundField = boundField;
        return this;
    }

    public Button getAddButton()
    {
        return addButton;
    }

    public RowCluster setAddButton(Button addButton)
    {
        Objects.requireNonNull(addButton, "addButton cannot be null");

        this.addButton = addButton;
        return this;
    }

    public Optional<ComponentEventListener<ClickEvent<Button>>> getAddButtonListener()
    {
        return Optional.ofNullable(addButtonListener);
    }

    public RowCluster setAddButtonListener(ComponentEventListener<ClickEvent<Button>> addButtonListener)
    {
        this.addButtonListener = addButtonListener;
        return this;
    }

    public Button getRemoveButton()
    {
        return removeButton;
    }

    public RowCluster setRemoveButton(Button removeButton)
    {
        Objects.requireNonNull(removeButton, "removeButton cannot be null");

        this.removeButton = removeButton;
        return this;
    }

    public Optional<ComponentEventListener<ClickEvent<Button>>> getRemoveButtonListener()
    {

        return Optional.ofNullable(removeButtonListener);
    }

    public RowCluster setRemoveButtonListener(ComponentEventListener<ClickEvent<Button>> removeButtonListener)
    {
        this.removeButtonListener = removeButtonListener;
        return this;
    }

    public List<ComponentBox> getComponentBoxes()
    {
        return componentBoxes;
    }

    public RowCluster setComponentBoxes(List<ComponentBox> componentBoxes)
    {
        Objects.requireNonNull(componentBoxes, "componentBoxes cannot be null");
        if(componentBoxes.contains(null))
            throw new NullPointerException("componentBoxes contains null");

        this.componentBoxes = componentBoxes;
        return this;
    }

    public void updateCluster()
    {
        this.removeAll();
        this.add(boundField);
        this.add(componentBoxes.stream().collect(Collectors.toSet()));

        //TODO Button add
        ruler();
    }

    private void ruler()
    {
        if(componentBoxes.isEmpty())
            this.add(addButton);
    }

    public void initDefaultAddButtonListener()
    {
        addButtonListener = event ->
        {

        };
    }

    public void initDefaultRemoveButtonListener()
    {
        removeButtonListener = event ->
        {

        };
    }

    private ComponentBox generateComponentBox(Collection<Component> items, int index)
    {
        ComponentBox componentBox = new ComponentBox();
        componentBox.setBound(bound);
        componentBox.setItems(items);

        addElseSet(componentBoxes, index, componentBox);
        return componentBox;
    }

    /**
     * Returns a collection of {@link de.bauersoft.data.entities.Component} objects where all recipes in each component
     * have at least one pattern that matches the given {@link DefaultPattern}.
     *
     * The method filters the provided collection of components. For each component, it checks all of its recipes to ensure
     * that every recipe has at least one pattern that matches the provided {@link DefaultPattern}. Only components where
     * all recipes meet this condition will be included in the returned collection.
     *
     * @param components the collection of {@link de.bauersoft.data.entities.Component} objects to be filtered
     * @param toMatch the {@link DefaultPattern} that the component's recipe patterns should match
     * @return a collection of components where all recipes contain at least one pattern that matches the specified {@link DefaultPattern}
     * @throws NullPointerException if the {@code components} is {@code null}
     */
    public static Collection<Component> getPatternMatchingComponents(Collection<de.bauersoft.data.entities.Component> components, DefaultPattern toMatch)
    {
        Objects.requireNonNull(components, "components cannot be null.");

        Collection<de.bauersoft.data.entities.Component> matching = components.stream()
                .filter(component -> component.getRecipes().stream()
                        .allMatch(recipe -> recipe.getPatterns().stream()
                                .anyMatch(pattern -> pattern.equalsDefault(toMatch))))
                .collect(Collectors.toList());

        return matching;
    }

    /**
     * Adds an element at the specified position in a copy of the given list.
     * If the index is equal to the size of the list, the element will be added at the end.
     * All subsequent elements will be shifted one position to the right.
     *
     * @param collection the collection to which the element will be added
     * @param index the position in the collection where the element will be added
     * @param element the element to add
     * @return a copy of the original collection with the added element
     * @param <E> the type of the element
     * @throws IndexOutOfBoundsException if index < 0 or index > size
     */
    public static  <E> List<E> addElseSet(List<E> collection, int index, E element)
    {
        List<E> copy = new ArrayList<>(collection);
        if(index < 0 || index > copy.size())
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);

        if(index == copy.size())
        {
            copy.add(element);

        }else copy.add(index, element);

        return copy;
    }

}
