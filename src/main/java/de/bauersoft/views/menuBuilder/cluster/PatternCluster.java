package de.bauersoft.views.menuBuilder.cluster;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.ListDataProvider;
import de.bauersoft.data.entities.Component;
import de.bauersoft.data.entities.Course;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.menu.MenuPatternComponent;
import de.bauersoft.data.entities.menu.MenuPatternComponents;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.repositories.component.ComponentRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.pattern.PatternRepository;
import de.bauersoft.views.menuBuilder.bounds.BoundComboBox;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.*;
import java.util.List;

public class PatternCluster extends ClusterLayout
{

    private MenuBuilderClusterManager clusterManager;

    private Pattern pattern;
    private Menu item;

    private ComponentRepository componentRepository;
    private CourseRepository courseRepository;
    private PatternRepository patternRepository;

    private Button removeButton;

    private TextField patternField;

    private Map<Course, ComboBox<Component>> componentBoxesMap;

    public PatternCluster(MenuBuilderClusterManager clusterManager, Pattern pattern)
    {
        this.clusterManager = clusterManager;

        this.pattern = pattern;
        this.item = clusterManager.getItem();

        this.componentRepository = clusterManager.getComponentRepository();
        this.courseRepository = clusterManager.getCourseRepository();
        this.patternRepository = clusterManager.getPatternRepository();

        removeButton = new Button();
        removeButton.setIcon(LineAwesomeIcon.MINUS_SQUARE.create());

        addComponent(removeButton);

        patternField = new TextField();
        patternField.setValue(pattern.getName());
        patternField.setTooltipText(pattern.getName());
        patternField.setReadOnly(true);
        patternField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);

        addComponent(patternField);

        componentBoxesMap = new HashMap<>();

        courseRepository.findAll().forEach(course -> setComponentBox(course));
        addComponentBoxes();

        updateComponents();
    }

    private PatternCluster compareComponent(Course course)
    {
        if(componentBoxesMap.get(course) == null) return this;
        if(componentBoxesMap.get(course).getValue() != null) return  this;

        DefaultCluster defaultCluster = clusterManager.getDefaultCluster();

        Collection<Pattern> hisPatterns = getMatchingPatternsForComponent(defaultCluster.getComponentBoxesMap().get(course).getValue());

        return this;
    }

    public PatternCluster loadPerhapsExistingData()
    {
        Optional<Map<Pattern, MenuPatternComponents>> map = item.getMenuPatternComponents();
        if(map.isEmpty()) return this;

        MenuPatternComponents variant = map.get().get(pattern);
        if(variant == null) return this;

        variant.getComponents().forEach(component ->
        {
            componentBoxesMap.get(component.getCourse()).setValue(component);
        });

        return this;
    }

    public ComboBox<Component> setComponentBox(Course course)
    {
        ComboBox<Component> componentBox = new ComboBox<>();

        componentBox.setItems(getPatternMatchingComponents(getCourseMatchingComponents(componentRepository.findAll(), course), pattern));
        componentBox.setItemLabelGenerator(item -> item.getName());

        componentBox.addValueChangeListener(event ->
        {
            componentBox.setTooltipText((event.getValue() == null) ? "" : event.getValue().getName());
        });

        componentBoxesMap.put(course, componentBox);
        return componentBox;
    }

    public PatternCluster addComponentBoxes()
    {
        this.addComponents(componentBoxesMap.values());
        return this;
    }

    public PatternCluster removeComponentBoxes()
    {
        this.removeComponents(componentBoxesMap.values());
        return this;
    }



    public Menu getItem()
    {
        return item;
    }

    public Pattern getPattern()
    {
        return pattern;
    }

    public Button getRemoveButton()
    {
        return removeButton;
    }

    public TextField getPatternField()
    {
        return patternField;
    }

    public Map<Course, ComboBox<Component>> getComponentBoxesMap()
    {
        return componentBoxesMap;
    }
}


