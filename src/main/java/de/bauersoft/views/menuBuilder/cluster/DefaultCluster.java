package de.bauersoft.views.menuBuilder.cluster;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import de.bauersoft.data.entities.Component;
import de.bauersoft.data.entities.Course;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.pattern.DefaultPattern;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.views.menuBuilder.bounds.BoundComboBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class DefaultCluster extends ClusterLayout
{
    private MenuBuilderClusterManager clusterManager;

    private Pattern pattern;
    private Menu item;

    private TextField patternField;

    private Map<Course, BoundComboBox<Pattern, Component>> componentBoxesMap;

    public DefaultCluster(MenuBuilderClusterManager clusterManager)
    {
        this.clusterManager = clusterManager;

        Optional<Pattern> defaultPattern = DefaultPattern.DEFAULT.getPattern(clusterManager.getPatternRepository());
        if(defaultPattern.isEmpty())
            throw new IllegalStateException("Default pattern could not be found. Try restarting the program.");

        pattern = defaultPattern.get();

        this.item = clusterManager.getItem();

        patternField = new TextField();
        patternField.setValue("Standard");
        patternField.setTooltipText("Standard");
        patternField.setReadOnly(true);
        patternField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);

        componentBoxesMap = new HashMap<>();

        addComponents(getPlaceholder(), patternField);
        clusterManager.getCourseRepository().findAll().forEach(course ->
        {
            setComponentBox(course);
        });

        addComponentBoxes();

        loadPerhapsExistingData();

        updateComponents();
    }

    private DefaultCluster loadPerhapsExistingData()
    {
        if(item.getId() == null) return this;

        List<Component> components = clusterManager.getMbComponentRepository().findMBComponentsByIds(item.getId(), pattern.getId());
        if(components == null) return this;

        for(Component component : components)
        {
            BoundComboBox<Pattern, Component> componentBox = componentBoxesMap.get(component.getCourse());
            if(componentBox == null) continue;

            componentBox.setValue(component);
        }

        return this;
    }

    public DefaultCluster saveCertainlyExistingData()
    {
        if(item.getId() == null)
            throw new IllegalStateException("Please save a menu item first so that JPA can assign an ID to it. Only then can you save this data.");

        for(BoundComboBox<Pattern, Component> componentBox : componentBoxesMap.values())
        {
            if(componentBox.getValue() == null) continue;

            clusterManager.getMbMenuRepository()
                    .upsertMenuPatternComponent(item.getId(), pattern.getId(), componentBox.getValue().getId());
        }

        return this;
    }

    private DefaultCluster compareComponent(Course course)
    {
        DefaultCluster defaultCluster = clusterManager.getDefaultCluster();

        return this;
    }

    private DefaultCluster setComponentBox(Course course)
    {
        BoundComboBox<Pattern, Component> componentBox = new BoundComboBox<>();
        componentBox.setClearButtonVisible(true);

        componentBox.setItems(clusterManager.getMbComponentRepository().findComponentsByCourseId(course.getId()));
        componentBox.setItemLabelGenerator(item -> item.getName());

        componentBox.addValueChangeListener(event ->
        {
            componentBox.setTooltipText((event.getValue() == null) ? "" : event.getValue().getName());
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



    public Pattern getPattern()
    {
        return pattern;
    }

    public Menu getItem()
    {
        return item;
    }

    public TextField getPatternField()
    {
        return patternField;
    }

    public Map<Course, BoundComboBox<Pattern, Component>> getComponentBoxesMap()
    {
        return componentBoxesMap;
    }
}
