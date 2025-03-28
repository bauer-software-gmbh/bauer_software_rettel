package de.bauersoft.views.menuBuilderNew.cluster;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.component.Component;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.entities.recipe.Recipe;
import de.bauersoft.services.ComponentService;
import de.bauersoft.views.menuBuilderNew.cluster.dialog.DescriptionDialog;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import javax.sound.sampled.Line;
import java.util.HashMap;
import java.util.Map;

@Getter
public class PatternCluster extends VerticalCluster
{
    private final ClusterManager clusterManager;
    private final Pattern pattern;

    private final ComponentService componentService;

    private final Button removeButton;
    private final Button descriptionButton;
    private final TextField patternNameField;

    private final Map<Course, ComboBox<Component>> comboBoxes;

    public PatternCluster(ClusterManager clusterManager, Pattern pattern)
    {
        this.clusterManager = clusterManager;
        this.pattern = pattern;

        this.componentService = clusterManager.getComponentService();

        removeButton = new Button(LineAwesomeIcon.MINUS_SQUARE_SOLID.create());

        descriptionButton = new Button("Beschreibung", LineAwesomeIcon.SCROLL_SOLID.create());
        descriptionButton.addClickListener(event ->
        {
            new DescriptionDialog(s ->
            {

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
            comboBox.setItemLabelGenerator(de.bauersoft.data.entities.component.Component::getName);
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
    }
}
