package de.bauersoft.views.menuBuilder.old;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.component.textfield.TextField;
import de.bauersoft.data.entities.Component;
import de.bauersoft.data.entities.Course;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.repositories.component.ComponentRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.pattern.PatternRepository;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.*;
import java.util.stream.Collectors;

public class ColumnCluster extends VerticalLayout
{
    private MenuBuilderCluster menuBuilderCluster;

    private ComponentRepository componentRepository;
    private PatternRepository patternRepository;
    private CourseRepository courseRepository;

    private Button addButton;
    private Button removeButton;
    private TextField placeholder;

    private ComboBox<Pattern> patternBox;

    private LinkedHashMap<Course, ComboBox<Component>> componentBoxes;


    public ColumnCluster(MenuBuilderCluster menuBuilderCluster, PatternRepository patternRepository, ComponentRepository componentRepository, CourseRepository courseRepository)
    {
        this.menuBuilderCluster = menuBuilderCluster;

        this.componentRepository = componentRepository;
        this.patternRepository = patternRepository;
        this.courseRepository = courseRepository;

        componentBoxes = new LinkedHashMap<>();

        this.setPadding(false);
        this.setSpacing(false);
        this.setWidth("200px");
        this.setAlignItems(FlexComponent.Alignment.STRETCH);

        placeholder = menuBuilderCluster.getPlaceholder();
        this.add(placeholder);

        addButton = new Button();
        addButton.setIcon(LineAwesomeIcon.PLUS_SQUARE.create());

        addButton.addClickListener(event ->
        {
           menuBuilderCluster.addColumnCluster();

           this.remove(addButton);
           this.remove(placeholder);

           this.add(removeButton);
           this.add(patternBox);
        });

        this.add(addButton);

        removeButton = new Button();
        removeButton.setIcon(LineAwesomeIcon.MINUS_SQUARE.create());

        patternBox = new ComboBox<>();
        patternBox.setItems(menuBuilderCluster.getPatternPoolDataProvider());
        patternBox.setItemLabelGenerator(item -> item.getName());

        patternBox.addValueChangeListener(event ->
        {
            menuBuilderCluster.removePatternPool(event.getValue());

            if(event.getOldValue() != null)
                menuBuilderCluster.addPatternPool(event.getOldValue());

            if(event.getValue() == null) return;

            courseRepository.findAll().forEach(course ->
            {
                this.add(addComponentBox(course));

            });

            updateComponentBoxes();
        });
    }

    private ComboBox<Component> addComponentBox(Course course)
    {
        Pattern pattern = patternBox.getValue();
        if(pattern == null) return null;

        ComboBox<Component> componentBox = new ComboBox<>();
        componentBox.setItems
                (
                        getPatternMatchingComponents(getCourseMatchingComponents(componentRepository.findAll(), course), pattern)
                );
        componentBox.setItemLabelGenerator(item -> item.getName());

        return componentBox;
    }

    private void removeComponentBoxes()
    {
        componentBoxes.clear();
    }

    private void updateComponentBoxes()
    {
        componentBoxes.entrySet().forEach(entry ->
        {
            this.remove(entry.getValue());
            this.add(entry.getValue());
        });
    }


    //TODO Dokumentation milan
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

    public Collection<Component> getPatternMatchingComponents(Collection<Component> components, Pattern toMatch)
    {
        Objects.requireNonNull(components, "components cannot be null");
        if(components.contains(null))
            throw new NullPointerException("components contains null");

        if(toMatch == null)
            return components;

        Collection<Component> matching = components.stream()
                .filter(component -> component.getRecipes().stream()
                        .allMatch(recipe -> recipe.getPatterns().stream()
                                .anyMatch(pattern -> pattern.equals(toMatch))))
                .collect(Collectors.toList());

        return matching;
    }

    public Button getAddButton()
    {
        return addButton;
    }

    public ColumnCluster setAddButton(Button addButton)
    {
        Objects.requireNonNull(addButton, "addButton cannot be null");

        this.addButton = addButton;
        return this;
    }

    public Button getRemoveButton()
    {
        return removeButton;
    }

    public ColumnCluster setRemoveButton(Button removeButton)
    {
        Objects.requireNonNull(removeButton, "removeButton cannot be null");

        this.removeButton = removeButton;
        return this;
    }
}
