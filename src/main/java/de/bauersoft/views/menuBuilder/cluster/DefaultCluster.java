package de.bauersoft.views.menuBuilder.cluster;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import de.bauersoft.data.entities.component.Component;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.pattern.DefaultPattern;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.entities.variant.Variant;
import de.bauersoft.views.menuBuilder.MenuBuilderPatternDescriptionDialog;
import de.bauersoft.views.menuBuilder.bounds.BoundComboBox;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.*;
import java.util.stream.Collectors;


public class DefaultCluster extends ClusterLayout
{
    private MenuBuilderClusterManager clusterManager;

    private Pattern pattern;
    private Menu item;

    private MenuBuilderPatternDescriptionDialog descriptionDialog;

    private Button descriptionButton;
    private TextField patternField;

    private Map<Course, BoundComboBox<Pattern, Component>> componentBoxesMap;
    private Variant variant;

    public DefaultCluster(MenuBuilderClusterManager clusterManager)
    {
        this.clusterManager = clusterManager;

        componentBoxesMap = new HashMap<>();

        Optional<Pattern> defaultPattern = DefaultPattern.DEFAULT.findPattern(clusterManager.getPatternRepository());
        if(defaultPattern.isEmpty())
            throw new IllegalStateException("Default pattern could not be found. Try restarting the programm.");

        pattern = defaultPattern.get();
        this.item = clusterManager.getItem();

        Optional<Variant> variant = clusterManager.getVariantService().getRepository().findByMenuIdAndPatternId(item.getId(), pattern.getId());
        if(variant.isPresent())
        {
            this.variant = variant.get();

        }else
        {
            this.variant = new Variant();
            this.variant.setMenu(item);
            this.variant.setPattern(pattern);
        }

        descriptionDialog = new MenuBuilderPatternDescriptionDialog(clusterManager, this.variant);

        descriptionButton = new Button("Beschreibung", LineAwesomeIcon.SCROLL_SOLID.create());
        descriptionButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        descriptionButton.setAriaLabel("Add item");

        descriptionButton.addClickListener(e ->
        {
            descriptionDialog.open();
        });

        patternField = new TextField();
        patternField.setValue("Standard");
        patternField.setTooltipText("Standard");
        patternField.setReadOnly(true);
        patternField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);

        addComponents(getPlaceholder(), descriptionButton, patternField);
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

        for(Component component : variant.getComponents())
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

        descriptionDialog.saveDescription();

        Set<Component> components = componentBoxesMap.values()
                .stream()
                .map(BoundComboBox::getValue)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        variant.setComponents(components);
        return this;
    }

    private DefaultCluster setComponentBox(Course course)
    {
        BoundComboBox<Pattern, Component> componentBox = new BoundComboBox<>();
        componentBox.setClearButtonVisible(true);

        componentBox.setItems(clusterManager.getComponentRepository().findByCourseId(course.getId()));
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


    public Variant getVariant()
    {
        return variant;
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
