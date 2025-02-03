package de.bauersoft.views.menuBuilder.cluster;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import de.bauersoft.data.entities.component.Component;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.entities.variant.Variant;
import de.bauersoft.views.menuBuilder.MenuBuilderPatternDescriptionDialog;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.*;
import java.util.stream.Collectors;

public class PatternCluster extends ClusterLayout
{

    private MenuBuilderClusterManager clusterManager;

    private Pattern pattern;
    private Menu item;

    private MenuBuilderPatternDescriptionDialog descriptionDialog;

    private Button descriptionButton;
    private Button removeButton;
    private TextField patternField;

    private Map<Course, ComboBox<Component>> componentBoxesMap;
    private Variant variant;

    public PatternCluster(MenuBuilderClusterManager clusterManager, Pattern pattern)
    {
        Objects.requireNonNull(clusterManager, "clusterManager cannot be null");
        Objects.requireNonNull(pattern, "pattern cannot be null");

        this.clusterManager = clusterManager;

        this.pattern = pattern;
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

        removeButton = new Button();
        removeButton.setIcon(LineAwesomeIcon.MINUS_SQUARE.create());

        addComponent(removeButton);

        patternField = new TextField();
        patternField.setValue(pattern.getName());
        patternField.setTooltipText(pattern.getName());
        patternField.setReadOnly(true);
        patternField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);

        addComponents(descriptionButton, patternField);

        componentBoxesMap = new HashMap<>();

        clusterManager.getCourseRepository().findAll().forEach(course -> setComponentBox(course));
        addComponentBoxes();

        loadPerhapsExistingData();

        updateComponents();
    }

    public PatternCluster loadPerhapsExistingData()
    {
        if(item.getId() == null) return this;

        for(Component component : variant.getComponents())
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

        descriptionDialog.saveDescription();

        Set<Component> components = componentBoxesMap.values()
                .stream()
                .map(ComboBox::getValue)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        variant.setComponents(components);
        return this;
    }

    public ComboBox<Component> setComponentBox(Course course)
    {
        ComboBox<Component> componentBox = new ComboBox<>();
        componentBox.setClearButtonVisible(true);

        componentBox.setItems(clusterManager.getComponentRepository().findComponentsByCourseIdAndPatternId(course.getId(), pattern.getId()));
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


