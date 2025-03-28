package de.bauersoft.views.menuBuilderNew.cluster;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.component.Component;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.pattern.DefaultPattern;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.services.ComponentService;
import de.bauersoft.services.PatternService;
import de.bauersoft.views.menuBuilderNew.cluster.dialog.DescriptionDialog;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class DefaultCluster extends VerticalCluster
{
    private final ClusterManager clusterManager;

    private final ComponentService componentService;
    private final PatternService patternService;

    private final Optional<Pattern> patternOptional;
    private final Pattern pattern;

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

        comboBoxes = new HashMap<>();

        descriptionButton = new Button("Beschreibung", LineAwesomeIcon.SCROLL_SOLID.create());
        descriptionButton.addClickListener(event ->
        {
            new DescriptionDialog(s ->
            {

            });
        });

        patternNameField  = new TextField();
        patternNameField.setWidthFull();
        patternNameField.setReadOnly(true);
        patternNameField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
        patternNameField.setValue(pattern.getName());

        this.add(getPlaceholder(), descriptionButton, patternNameField);

        for(Course course : clusterManager.getCoursePool())
        {
            ComboBox<Component> comboBox = new ComboBox<>();
            comboBox.setWidthFull();
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
    }

    private TextField getPlaceholder()
    {
        TextField placeholderField = new TextField();
        placeholderField.setWidthFull();
        placeholderField.setReadOnly(true);

        return placeholderField;
    }
}
