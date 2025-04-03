package de.bauersoft.views.menuBuilderNew.components.clusters;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.component.Component;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.pattern.DefaultPattern;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.entities.variant.Variant;
import de.bauersoft.services.ComponentService;
import de.bauersoft.services.PatternService;
import de.bauersoft.views.menuBuilderNew.components.ClusterManager;
import de.bauersoft.views.menuBuilderNew.components.container.VariantContainer;
import de.bauersoft.views.menuBuilderNew.components.container.VariantMapContainer;
import de.bauersoft.views.menuBuilderNew.components.dialog.DescriptionDialog;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class DefaultCluster extends VerticalCluster
{
    private final ClusterManager clusterManager;

    private final ComponentService componentService;
    private final PatternService patternService;

    private final Optional<Pattern> patternOptional;
    private final Pattern pattern;

    private final VariantMapContainer mapContainer;
    private final VariantContainer container;

    private final Button descriptionButton;
    private final TextField patternNameField;

    private final Map<Course, ComboBox<Component>> comboBoxes;

    public DefaultCluster(ClusterManager clusterManager)
    {
        this.clusterManager = clusterManager;

        this.componentService = clusterManager.getComponentService();
        this.patternService = clusterManager.getPatternService();

        patternOptional = DefaultPattern.DEFAULT.findPattern(patternService.getRepository());
        if(patternOptional.isEmpty())
        {
            Notification notification = new Notification();
            notification.setDuration(Integer.MAX_VALUE);
            notification.setPosition(Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setText("""
                    Die Ernährungsform 'Normal' existiert nicht unter ihrem Standardmuster.
                    Bitte kontaktieren Sie einen Administrator.
                    """);

            throw new IllegalStateException("Die Ernährungsform 'Normal' existiert nicht unter ihrem Standardmuster.");
        }

        pattern = patternOptional.get();

        mapContainer = clusterManager.getVariantMapContainer();

        container = (VariantContainer) mapContainer.addIfAbsent(pattern, () ->
        {
            Variant variant = new Variant();
            variant.setMenu(clusterManager.getItem());
            variant.setPattern(pattern);

            return variant;
        }, ContainerState.NEW);

        descriptionButton = new Button("Beschreibung", LineAwesomeIcon.SCROLL_SOLID.create());
        descriptionButton.addClickListener(event ->
        {
            new DescriptionDialog(container.getTempDescription(), s ->
            {
                container.setTempDescription(s);
                container.setTempState(ContainerState.UPDATE);
            });
        });

        patternNameField  = new TextField();
        patternNameField.setWidthFull();
        patternNameField.setReadOnly(true);
        patternNameField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
        patternNameField.setValue(pattern.getName());

        this.add(getPlaceholder(), descriptionButton, patternNameField);

        comboBoxes = new HashMap<>();
        for(Course course : clusterManager.getCoursePool())
        {
            ComboBox<Component> comboBox = new ComboBox<>();
            comboBox.setWidthFull();
            comboBox.setClearButtonVisible(true);
            comboBox.setItemLabelGenerator(Component::getName);
            comboBox.setItems(query ->
            {
                return FilterDataProvider.lazyFilteredStream(componentService, query, (root, criteriaQuery, criteriaBuilder, filterInput) ->
                {
                    return criteriaBuilder.and(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), filterInput + "%"),
                            criteriaBuilder.equal(root.get("course").get("id"), course.getId())
                    );
                });
            });

            comboBoxes.put(course, comboBox);
            this.add(comboBox);
        }

        loadComponents();

        comboBoxes.values().forEach(comboBox ->
        {
            comboBox.addValueChangeListener(event ->
            {
                container.setTempComponents(comboBoxes.values()
                        .stream()
                        .map(ComboBox::getValue)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()));
                container.setTempState(ContainerState.UPDATE);

                if(event.getValue() == null)
                    comboBox.setTooltipText("");
            });
        });
    }

    private TextField getPlaceholder()
    {
        TextField placeholderField = new TextField();
        placeholderField.setWidthFull();
        placeholderField.setReadOnly(true);

        return placeholderField;
    }

    private DefaultCluster loadComponents()
    {
        Variant variant = container.getEntity();
        variant.getComponents()
                .stream()
                .filter(component -> comboBoxes.containsKey(component.getCourse()))
                .forEach(component ->
                {
                    ComboBox<Component> comboBox = comboBoxes.get(component.getCourse());
                    comboBox.setValue(component);
                    comboBox.setTooltipText(component.getName());
                });

        return this;
    }
}
