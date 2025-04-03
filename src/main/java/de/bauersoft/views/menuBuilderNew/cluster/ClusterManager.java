package de.bauersoft.views.menuBuilderNew.cluster;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.pattern.DefaultPattern;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.services.*;
import de.bauersoft.services.offer.OfferService;
import de.bauersoft.views.menuBuilderNew.cluster.dialog.DescriptionDialog;
import de.bauersoft.views.menuBuilderNew.cluster.dialog.PatternDialog;
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

        patternClusters = new HashMap<>();

        defaultCluster = new DefaultCluster(this);

        courseCluster = new CourseCluster(this);
        courseCluster.getAddButton().addClickListener(event ->
        {
            new PatternDialog(patternDataProvider, pattern ->
            {
                PatternCluster patternCluster = patternClusters.computeIfAbsent(pattern, p ->
                {
                    PatternCluster cluster = new PatternCluster(this, p);
                    cluster.getRemoveButton().addClickListener(e ->
                    {
                        patternPool.add(p);
                        patternDataProvider.refreshAll();

                        this.remove(cluster);
                    });

                    return cluster;
                });

                this.add(patternCluster);

                patternPool.remove(pattern);
                patternDataProvider.refreshAll();
            });
        });


        this.add(courseCluster, defaultCluster);
        this.setWidthFull();
        this.getStyle()
                .setPaddingTop("var(--lumo-space-s)");
    }
}
