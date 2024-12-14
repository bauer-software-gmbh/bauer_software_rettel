package de.bauersoft.views.menuBuilder.cluster;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import de.bauersoft.data.entities.Component;
import de.bauersoft.data.entities.Course;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.menu.MenuPatternComponent;
import de.bauersoft.data.entities.menu.MenuPatternComponents;
import de.bauersoft.data.entities.pattern.DefaultPattern;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.repositories.component.ComponentRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import org.hibernate.type.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class DefaultCluster extends ClusterLayout
{
    private MenuBuilderClusterManager clusterManager;
    private Menu item;

    private DefaultPattern pattern;

    private ComponentRepository componentRepository;
    private CourseRepository courseRepository;

    private TextField patternField;

    private Map<Course, ComboBox<Component>> componentBoxesMap;

    public DefaultCluster(MenuBuilderClusterManager clusterManager)
    {
        this.clusterManager = clusterManager;
        this.item = clusterManager.getItem();

        this.pattern = DefaultPattern.DEFAULT;

        this.componentRepository = clusterManager.getComponentRepository();
        this.courseRepository = clusterManager.getCourseRepository();

        patternField = new TextField();
        patternField.setValue("Standard");
        patternField.setTooltipText("Standard");
        patternField.setReadOnly(true);
        patternField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);

        componentBoxesMap = new HashMap<>();

        addComponents(getPlaceholder(), patternField);
        courseRepository.findAll().forEach(course ->
        {
            setComponentBox(course);
        });

        addComponentBoxes();

        updateComponents();
    }

    private DefaultCluster compareComponent(Course course)
    {
        DefaultCluster defaultCluster = clusterManager.getDefaultCluster();

        return this;
    }

    private DefaultCluster setComponentBox(Course course)
    {
        ComboBox<Component> componentBox = new ComboBox<>();

        componentBox.setItems(getCourseMatchingComponents(componentRepository.findAll(), course));
        componentBox.setItemLabelGenerator(item -> item.getName());

        componentBox.addValueChangeListener(event ->
        {
            componentBox.setTooltipText((event.getValue() == null) ? "" : event.getValue().getName());

            clusterManager.getPatternClusters().forEach(patternCluster ->
            {
                boolean allesOke = clusterManager.allesOke(patternCluster.getPattern(), course);
                patternCluster.getComponentBoxesMap().get(course).setInvalid(!allesOke);
            });
        });

        componentBoxesMap.put(course, componentBox);
        return this;
    }

    private DefaultCluster addComponentBoxes()
    {
        this.addComponents(componentBoxesMap.values());
        return this;
    }

    private DefaultCluster removeComponentBoxes()
    {
        this.removeComponents(componentBoxesMap.values());
        return this;
    }



    public Menu getItem()
    {
        return item;
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
