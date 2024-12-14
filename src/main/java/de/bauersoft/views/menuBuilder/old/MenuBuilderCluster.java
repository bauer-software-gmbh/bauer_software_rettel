package de.bauersoft.views.menuBuilder.old;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.ListDataProvider;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.repositories.component.ComponentRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.pattern.PatternRepository;

import java.util.ArrayList;
import java.util.List;

public class MenuBuilderCluster extends HorizontalLayout
{

    private PatternRepository patternRepository;
    private ComponentRepository componentRepository;
    private CourseRepository courseRepository;

    private VerticalLayout boundsLayout;

    private List<ColumnCluster> columnClusters;

    private List<Pattern> patternPool;
    private ListDataProvider<Pattern> patternPoolDataProvider;


    public MenuBuilderCluster(PatternRepository patternRepository, ComponentRepository componentRepository, CourseRepository courseRepository)
    {
        this.patternRepository = patternRepository;
        this.componentRepository = componentRepository;
        this.courseRepository = courseRepository;

        columnClusters = new ArrayList<>();

        patternPool = new ArrayList<>(patternRepository.findAll());
        patternPoolDataProvider = new ListDataProvider<>(patternPool);


        boundsLayout = new VerticalLayout();
        boundsLayout.setPadding(false);
        boundsLayout.setSpacing(false);
        boundsLayout.setWidth("200px");
        boundsLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        for(int i = 0; i < 2; i++)
            boundsLayout.add(getPlaceholder());

        courseRepository.findAll().forEach(bound ->
        {
            TextField boundField = new TextField();
            boundField.setValue(bound.getName());
            boundField.setTooltipText(bound.getName());
            boundField.setReadOnly(true);
            boundField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);

            boundsLayout.add(boundField);
        });

        this.add(boundsLayout);

        addColumnCluster();
    }

    public void addColumnCluster()
    {
        ColumnCluster columnCluster = new ColumnCluster(this, patternRepository, componentRepository, courseRepository);

        columnClusters.add(columnCluster);
        this.add(columnCluster);
    }

    public List<ColumnCluster> getColumnClusters()
    {
        return columnClusters;
    }

    public MenuBuilderCluster setColumnClusters(List<ColumnCluster> columnClusters)
    {
        this.columnClusters = columnClusters;
        return this;
    }

    public TextField getPlaceholder()
    {
        TextField placeholder = new TextField();
        placeholder.setReadOnly(true);

        return placeholder;
    }

    public void addPatternPool(Pattern pattern)
    {
        patternPool.add(pattern);
        patternPoolDataProvider.refreshAll();
    }

    public void removePatternPool(Pattern pattern)
    {
        patternPool.remove(pattern);
        patternPoolDataProvider.refreshAll();
    }

    public List<Pattern> getPatternPool()
    {
        return patternPool;
    }

    public ListDataProvider<Pattern> getPatternPoolDataProvider()
    {
        return patternPoolDataProvider;
    }
}
