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
import de.bauersoft.data.repositories.component.ComponentRepository;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.*;
import java.util.stream.Collectors;

public class RowCluster extends FlexLayout
{
    private Course bound;
    private TextField boundField;

    private Button addButton;
    private ComponentEventListener<ClickEvent<Button>> addButtonClickListener;

    private Button removeButton;
    private ComponentEventListener<ClickEvent<Button>> removeButtonClickListener;

    private List<ComponentBox> componentBoxes;

    private ComponentRepository componentRepository;

    public RowCluster(Course bound, ComponentRepository componentRepository)
    {
        Objects.requireNonNull(bound);

        this.bound = bound;
        this.componentRepository = componentRepository;

        boundField = new TextField();
        boundField.setValue(bound.getName());
        boundField.getElement().getStyle().set("margin", "5px");
        boundField.setWidth("calc(20% - 10px)");
        boundField.setReadOnly(true);
        boundField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);

        addButton = new Button();
        addButton.setIcon(LineAwesomeIcon.PLUS_SQUARE.create());
        addButton.getElement().getStyle().set("margin", "5px");

        addButtonClickListener = event ->
        {
            Collection<Component> items = componentRepository.findAll();

            items = getCourseMatchingComponents(items, bound);
            items = getPatternMatchingComponents(items, DefaultPattern.VEGAN);

            addComponentBox(items);

            updateCluster();
        };

        addButton.addClickListener(addButtonClickListener);

        removeButton = new Button();
        removeButton.setIcon(LineAwesomeIcon.MINUS_SQUARE.create());
        removeButton.getElement().getStyle().set("margin", "5px");

        removeButtonClickListener = event ->
        {
            componentBoxes.clear();
            updateCluster();
        };

        removeButton.addClickListener(removeButtonClickListener);

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

    public ComponentEventListener<ClickEvent<Button>> getAddButtonClickListener()
    {
        return addButtonClickListener;
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

    public ComponentEventListener<ClickEvent<Button>> getRemoveButtonClickListener()
    {
        return removeButtonClickListener;
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


        if(componentBoxes.size() < 1)
            this.add(addButton);
        else
            this.add(removeButton);

        //ruler();
    }

    private void ruler()
    {
        if(componentBoxes.isEmpty())
            this.add(addButton);
    }


    private ComponentBox addComponentBox(Collection<Component> items)
    {
        ComponentBox componentBox = new ComponentBox(bound);
        componentBox.setItems(items);

        componentBox.setItemLabelGenerator(component -> component.getName());

        componentBoxes.add(componentBox);
        return componentBox;
    }

    public static Collection<Component> getPatternMatchingComponents(Collection<Component> components, DefaultPattern toMatch)
    {
        Objects.requireNonNull(components, "components cannot be null");
        if(components.contains(null))
            throw new NullPointerException("components contains null");

        Objects.requireNonNull(toMatch, "toMatch cannot be null");

        Collection<Component> matching = components.stream()
                .filter(component -> component.getRecipes().stream()
                        .allMatch(recipe -> recipe.getPatterns().stream()
                                .anyMatch(pattern -> pattern.equalsDefault(toMatch))))
                .collect(Collectors.toList());

        return matching;
    }

    public static Collection<Component> getCourseMatchingComponents(Collection<Component> components, Course toMatch)
    {
        Objects.requireNonNull(components, "components cannot be null");
        if(components.contains(null))
            throw new NullPointerException("components contains null");

        Objects.requireNonNull(toMatch, "course cannot be null");

        Collection<Component> matching = components.stream()
                .filter(component -> component.getCourse().equals(toMatch))
                .collect(Collectors.toList());

        return matching;
    }

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
