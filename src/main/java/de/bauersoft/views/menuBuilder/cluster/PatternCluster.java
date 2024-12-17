package de.bauersoft.views.menuBuilder.cluster;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import de.bauersoft.data.entities.Component;
import de.bauersoft.data.entities.Course;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.pattern.Pattern;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatternCluster extends ClusterLayout
{

    private MenuBuilderClusterManager clusterManager;
    private Pattern pattern;
    private Menu item;

    private Button removeButton;

    private TextField patternField;

    private Map<Course, ComboBox<Component>> componentBoxesMap;

    public PatternCluster(MenuBuilderClusterManager clusterManager, Pattern pattern)
    {
        this.clusterManager = clusterManager;

        this.pattern = pattern;
        this.item = clusterManager.getItem();

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

        clusterManager.getCourseRepository().findAll().forEach(course -> setComponentBox(course));
        addComponentBoxes();

        loadPerhapsExistingData();

        updateComponents();
    }

    public PatternCluster loadPerhapsExistingData()
    {
        if(item.getId() == null) return this;

        List<Component> components = clusterManager.getMbComponentRepository().findMBComponentsByIds(item.getId(), pattern.getId());
        if(components == null) return this;

        for(Component component : components)
        {
            ComboBox<Component> componentBox = componentBoxesMap.get(component.getCourse());
            if(componentBox == null) continue;

            componentBox.setValue(component);
        }

        return this;
    }

    public PatternCluster saveCertainlyExistingData()
    {
        if(item.getId() == null)
            throw new IllegalStateException("Please save a menu item first so that JPA can assign an ID to it. Only then can you save this data.");

        for(ComboBox<Component> componentBox : componentBoxesMap.values())
        {
            if(componentBox.getValue() == null) continue;

            clusterManager.getMbMenuRepository()
                    .upsertMenuPatternComponent(item.getId(), pattern.getId(), componentBox.getValue().getId());
        }

        return this;
    }

    public ComboBox<Component> setComponentBox(Course course)
    {
        ComboBox<Component> componentBox = new ComboBox<>();
        componentBox.setClearButtonVisible(true);

        componentBox.setItems(clusterManager.getMbComponentRepository().findComponentsByIds(pattern.getId(), course.getId()));
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



    public Pattern getPattern()
    {
        return pattern;
    }

    public Menu getItem()
    {
        return item;
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


