package de.bauersoft.views.menuBuilderNew.components.clusters;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.comboBox.BoundComboBox;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.component.Component;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.entities.recipe.Recipe;
import de.bauersoft.data.entities.variant.Variant;
import de.bauersoft.services.ComponentService;
import de.bauersoft.views.menuBuilderNew.components.ClusterManager;
import de.bauersoft.views.menuBuilderNew.components.container.VariantContainer;
import de.bauersoft.views.menuBuilderNew.components.container.VariantMapContainer;
import de.bauersoft.views.menuBuilderNew.components.dialog.DescriptionDialog;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.Getter;
import org.aspectj.weaver.ast.Var;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class PatternCluster extends VerticalCluster
{
    private final ClusterManager clusterManager;
    private final Pattern pattern;

    private final ComponentService componentService;

    private final VariantMapContainer mapContainer;
    private final VariantContainer container;

    private final Button removeButton;
    private final Button descriptionButton;
    private final TextField patternNameField;

    private final Map<Course, ComboBox<Component>> comboBoxes;

    public PatternCluster(ClusterManager clusterManager, Pattern pattern)
    {
        this.clusterManager = clusterManager;
        this.pattern = pattern;

        this.componentService = clusterManager.getComponentService();

        mapContainer = clusterManager.getVariantMapContainer();
        container = (VariantContainer) mapContainer.addIfAbsent(pattern, () ->
        {
            Variant variant = new Variant();
            variant.setMenu(clusterManager.getItem());
            variant.setPattern(pattern);

            return variant;
        }, ContainerState.NEW);

        removeButton = new Button(LineAwesomeIcon.MINUS_SQUARE_SOLID.create());

        descriptionButton = new Button("Beschreibung", LineAwesomeIcon.SCROLL_SOLID.create());
        descriptionButton.addClickListener(event ->
        {
            new DescriptionDialog(container.getTempDescription(), s ->
            {
                container.setTempDescription(s);
                container.setTempState(ContainerState.UPDATE);
            });
        });

        patternNameField = new TextField();
        patternNameField.setWidthFull();
        patternNameField.setReadOnly(true);
        patternNameField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
        patternNameField.setValue(pattern.getName());

        this.add(removeButton, descriptionButton, patternNameField);

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
                    Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
                    Root<Component> subRoot = subquery.from(Component.class);
                    Join<Component, Recipe> recipeJoin = subRoot.join("recipes");
                    Join<Recipe, Pattern> patternJoin = recipeJoin.join("patterns");

                    subquery.select(subRoot.get("id"))
                            .where(criteriaBuilder.and(
                                    criteriaBuilder.equal(subRoot.get("id"), root.get("id")),
                                    criteriaBuilder.equal(patternJoin.get("id"), pattern.getId())
                            ))
                            .groupBy(subRoot.get("id"))
                            .having(criteriaBuilder.equal(criteriaBuilder.countDistinct(recipeJoin.get("id")), criteriaBuilder.countDistinct(patternJoin.get("id"))));

                    return criteriaBuilder.and(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), filterInput + "%"),
                            criteriaBuilder.equal(root.get("course").get("id"), course.getId()),
                            criteriaBuilder.exists(subquery)
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

    private PatternCluster loadComponents()
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
