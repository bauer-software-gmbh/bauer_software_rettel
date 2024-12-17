package de.bauersoft.views.menuBuilder.cluster;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import de.bauersoft.data.entities.Menu;
import de.bauersoft.data.entities.pattern.DefaultPattern;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.repositories.component.ComponentRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.menuBuilder.MBComponentRepository;
import de.bauersoft.data.repositories.menuBuilder.MBMenuRepository;
import de.bauersoft.data.repositories.menuBuilder.MBPatternRepository;
import de.bauersoft.data.repositories.pattern.PatternRepository;
import de.bauersoft.views.menuBuilder.MenuBuilderPatternSelectorDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MenuBuilderClusterManager extends HorizontalLayout
{
    private Menu item;

    private ComponentRepository componentRepository;
    private MBMenuRepository mbMenuRepository;
    private MBComponentRepository mbComponentRepository;
    private MBPatternRepository mbPatternRepository;
    private CourseRepository courseRepository;
    private PatternRepository patternRepository;

    private CourseCluster courseCluster;
    private DefaultCluster defaultCluster;
    private List<PatternCluster> patternClusters;

    private List<Pattern> patternPool;
    private ListDataProvider<Pattern> patternPoolDataProvider;

    public MenuBuilderClusterManager(Menu item, MBMenuRepository mbMenuRepository, MBComponentRepository mbComponentRepository, MBPatternRepository mbPatternRepository, ComponentRepository componentRepository, CourseRepository courseRepository, PatternRepository patternRepository)
    {
        this.item = item;
        this.componentRepository = componentRepository;
        this.mbMenuRepository = mbMenuRepository;
        this.mbComponentRepository = mbComponentRepository;
        this.mbPatternRepository = mbPatternRepository;
        this.courseRepository = courseRepository;
        this.patternRepository = patternRepository;

        courseCluster = new CourseCluster(courseRepository);
        defaultCluster = new DefaultCluster(this);
        patternClusters = new ArrayList<>();

        patternPool = new ArrayList<>
                (
                        patternRepository.findAll().stream()
                                .filter(pattern -> !DefaultPattern.DEFAULT.equalsDefault(pattern))
                                .collect(Collectors.toList())
                );
        patternPoolDataProvider = new ListDataProvider<>(patternPool);

        courseCluster.getAddButton().addClickListener(event ->
        {
            new MenuBuilderPatternSelectorDialog(this);
        });

        loadPerhapsExistingData();

        updateClusters();
    }

    //Prototype
//    public boolean allesOke(Pattern pattern, Course course)
//    {
//        Component defaultComponent = defaultCluster.getComponentBoxesMap().get(course).getValue();
//        if(defaultComponent == null)
//            return true;
//
//        if(patternClusters.stream().filter(patternCluster -> patternCluster.getPattern().equals(pattern)).findFirst().get().getComponentBoxesMap().get(course).getValue() != null)
//            return true;
//
//        List<Pattern> matching = CourseCluster.getMatchingPatternsForComponent(defaultComponent);
//
//        if(matching.contains(pattern))
//        {
//            return true;
//        }else
//        {
//            return false;
//        }
//    }

    private MenuBuilderClusterManager loadPerhapsExistingData()
    {
        if(item.getId() == null) return this;

        List<Pattern> patterns = mbPatternRepository.findMBPatternsByMenuId(item.getId());
        for(Pattern pattern : patterns)
        {
            if(DefaultPattern.DEFAULT.equalsDefault(pattern)) continue;

            addPatternCluster(pattern);
            removePatternPool(pattern);
        }

        updateClusters();

        return this;
    }

    public PatternCluster addPatternCluster(Pattern pattern)
    {
        PatternCluster patternCluster = new PatternCluster(this, pattern);
        patternCluster.getRemoveButton().addClickListener(event ->
        {
            addPatternPool(patternCluster.getPattern());

            removePatternCluster(patternCluster);
            updateClusters();
        });

        patternClusters.add(patternCluster);
        return patternCluster;
    }

    public MenuBuilderClusterManager removePatternCluster(PatternCluster patternCluster)
    {
        patternClusters.remove(patternCluster);
        return this;
    }



    public void updateClusters()
    {
        this.removeAll();
        this.add(courseCluster, defaultCluster);

        patternClusters.forEach(patternCluster -> this.add(patternCluster));
    }



    public CourseCluster getCourseCluster()
    {
        return courseCluster;
    }

    public DefaultCluster getDefaultCluster()
    {
        return defaultCluster;
    }

    public List<PatternCluster> getPatternClusters()
    {
        return patternClusters;
    }



    public MenuBuilderClusterManager addPatternPool(Pattern pattern)
    {
        Objects.requireNonNull(pattern, "pattern cannot be null");

        if(!patternPool.contains(pattern))
            patternPool.add(pattern);

        patternPoolDataProvider.refreshAll();
        return this;
    }

    public MenuBuilderClusterManager removePatternPool(Pattern pattern)
    {
        Objects.requireNonNull(pattern, "pattern cannot be null");

        patternPool.remove(pattern);
        patternPoolDataProvider.refreshAll();
        return this;
    }

    public List<Pattern> getPatternPool()
    {
        return patternPool;
    }

    public MenuBuilderClusterManager setPatternPool(List<Pattern> patternPool)
    {
        Objects.requireNonNull(patternPool, "patternPool cannot be null");

        this.patternPool = patternPool;
        return this;
    }

    public ListDataProvider<Pattern> getPatternPoolDataProvider()
    {
        return patternPoolDataProvider;
    }

    public MenuBuilderClusterManager setPatternPoolDataProvider(ListDataProvider<Pattern> patternPoolDataProvider)
    {
        Objects.requireNonNull(patternPoolDataProvider, "patternPoolDataProvider cannot be null");

        this.patternPoolDataProvider = patternPoolDataProvider;
        return this;
    }



    public Menu getItem()
    {
        return item;
    }

    public MBMenuRepository getMbMenuRepository()
    {
        return mbMenuRepository;
    }

    public MBComponentRepository getMbComponentRepository()
    {
        return mbComponentRepository;
    }

    public MBPatternRepository getMbPatternRepository()
    {
        return mbPatternRepository;
    }

    public ComponentRepository getComponentRepository()
    {
        return componentRepository;
    }

    public CourseRepository getCourseRepository()
    {
        return courseRepository;
    }

    public PatternRepository getPatternRepository()
    {
        return patternRepository;
    }
}
