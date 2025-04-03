package de.bauersoft.views.menuBuilderNew.components;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.pattern.DefaultPattern;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.entities.variant.Variant;
import de.bauersoft.services.*;
import de.bauersoft.services.offer.OfferService;
import de.bauersoft.views.menuBuilderNew.components.clusters.CourseCluster;
import de.bauersoft.views.menuBuilderNew.components.clusters.DefaultCluster;
import de.bauersoft.views.menuBuilderNew.components.clusters.PatternCluster;
import de.bauersoft.views.menuBuilderNew.components.container.VariantContainer;
import de.bauersoft.views.menuBuilderNew.components.container.VariantMapContainer;
import de.bauersoft.views.menuBuilderNew.components.dialog.PatternDialog;
import lombok.Getter;

import java.util.*;

@Getter
public class ClusterManager extends HorizontalLayout
{
    private final MenuService menuService;
    private final CourseService courseService;
    private final ComponentService componentService;
    private final PatternService patternService;
    private final RecipeService recipeService;
    private final VariantService variantService;
    private final FleshService fleshService;
    private final OfferService offerService;
    private final OrderDataService orderDataService;

    private final Menu item;

    private List<Course> coursePool;
    private List<Pattern> patternPool;
    private ListDataProvider<Pattern> patternDataProvider;

    private final VariantMapContainer variantMapContainer;

    private final Map<Pattern, PatternCluster> patternClusters;
    private final DefaultCluster defaultCluster;
    private final CourseCluster courseCluster;

    public ClusterManager(MenuService menuService, CourseService courseService, ComponentService componentService, PatternService patternService, RecipeService recipeService, VariantService variantService, FleshService fleshService, OfferService offerService, OrderDataService orderDataService, Menu item)
    {
        this.menuService = menuService;
        this.courseService = courseService;
        this.componentService = componentService;
        this.patternService = patternService;
        this.recipeService = recipeService;
        this.variantService = variantService;
        this.fleshService = fleshService;
        this.offerService = offerService;
        this.orderDataService = orderDataService;
        this.item = item;

        coursePool = new ArrayList<>(courseService.findAll());
        coursePool.sort(Comparator.comparing(Course::getName));

        patternPool = new ArrayList<>(patternService.findAll());
        patternPool.removeIf(pattern -> DefaultPattern.DEFAULT.equalsDefault(pattern));
        patternDataProvider = new ListDataProvider<>(patternPool);

        variantMapContainer = new VariantMapContainer();
        for(Variant variant : item.getVariants())
            variantMapContainer.addContainer(variant.getPattern(), variant, ContainerState.SHOW);



        courseCluster = new CourseCluster(this);
        defaultCluster = new DefaultCluster(this);

        this.add(courseCluster, defaultCluster);



        patternClusters = new HashMap<>();
        variantMapContainer.getContainerMap().entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals(defaultCluster.getPattern()))
                .forEach(entry ->
                {
                    addPatternCluster(entry.getKey());
                });

        courseCluster.getAddButton().addClickListener(event ->
        {
            new PatternDialog(patternDataProvider, pattern ->
            {
                addPatternCluster(pattern);
            });
        });

        this.setWidthFull();
        this.getStyle()
                .setPaddingTop("var(--lumo-space-s)");
    }

    private ClusterManager addPatternCluster(Pattern pattern)
    {
        Objects.requireNonNull(pattern);;

        PatternCluster patternCluster = patternClusters.computeIfAbsent(pattern, p ->
        {
            PatternCluster cluster = new PatternCluster(this, p);
            cluster.getRemoveButton().addClickListener(e ->
            {
                VariantContainer container = cluster.getContainer();
                if(container.getState() == ContainerState.NEW)
                    container.setTempState(ContainerState.HIDE);
                else container.setTempState(ContainerState.DELETE);

                patternPool.add(p);
                patternDataProvider.refreshAll();

                this.remove(cluster);
            });

            return cluster;
        });

        this.add(patternCluster);

        patternPool.remove(pattern);
        patternDataProvider.refreshAll();

        return this;
    }
}
