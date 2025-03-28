package de.bauersoft.views.institution.institutionFields;

import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import de.bauersoft.components.autofilter.Filter;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergen;
import de.bauersoft.data.entities.institutionFieldMultiplier.InstitutionMultiplier;
import de.bauersoft.data.entities.institutionFieldPattern.InstitutionPattern;
import de.bauersoft.services.*;
import de.bauersoft.views.institution.institutionFields.components.allergen.AllergenMapContainer;
import de.bauersoft.views.institution.institutionFields.components.multiplier.MultiplierMapContainer;
import de.bauersoft.views.institution.institutionFields.components.pattern.PatternMapContainer;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import org.atmosphere.config.service.Get;
import org.vaadin.lineawesome.LineAwesomeIcon;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class InstitutionFieldComponent extends HorizontalLayout
{
    private final InstitutionService institutionService;
    private final FieldService fieldService;
    private final InstitutionFieldService institutionFieldService;
    private final InstitutionPatternService institutionPatternService;
    private final PatternService patternService;
    private final InstitutionMultiplierService institutionMultiplierService;
    private final FieldMultiplierService fieldMultiplierService;
    private final CourseService courseService;
    private final InstitutionAllergenService institutionAllergenService;
    private final AllergenService allergenService;

    private final Institution item;

    private final FilterDataProvider<Field, Long> fieldDataProvider;
    private Filter<Field> fieldFilter;
    private Filter<Field> fieldNameFilter;

    private final InstitutionFieldMapContainer mapContainer;

    private final InstitutionFieldGrid institutionFieldGrid;
    private final InstitutionFieldList institutionFieldList;

    private final Map<Field, PatternMapContainer> patternMapContainers;
    private final Map<Field, MultiplierMapContainer> multiplierMapContainers;
    private final Map<Field, AllergenMapContainer> allergenMapContainers;

    public InstitutionFieldComponent(InstitutionService institutionService, FieldService fieldService, InstitutionFieldService institutionFieldService, InstitutionPatternService institutionPatternService, PatternService patternService, InstitutionMultiplierService institutionMultiplierService, FieldMultiplierService fieldMultiplierService, CourseService courseService, InstitutionAllergenService institutionAllergenService, AllergenService allergenService, Institution item)
    {
        this.institutionService = institutionService;
        this.fieldService = fieldService;
        this.institutionFieldService = institutionFieldService;
        this.institutionPatternService = institutionPatternService;
        this.patternService = patternService;
        this.institutionMultiplierService = institutionMultiplierService;
        this.fieldMultiplierService = fieldMultiplierService;
        this.courseService = courseService;
        this.institutionAllergenService = institutionAllergenService;
        this.allergenService = allergenService;
        this.item = item;

        fieldDataProvider = new FilterDataProvider<>(fieldService);
        fieldFilter = new Filter<Field>("name", (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            List<Field> doNotShow = new ArrayList<>(
                    getItems().get(true)
                            .stream()
                            .map(container -> container.getEntity().getField())
                            .collect(Collectors.toList())
            );

            if(!doNotShow.isEmpty())
            {
                CriteriaBuilder.In<Long> inClause = criteriaBuilder.in(root.get("id"));
                for(Field field : doNotShow)
                    inClause.value(field.getId());

                return criteriaBuilder.not(inClause);
            }

            return criteriaBuilder.conjunction();
        }).setIgnoreFilterInput(true);

        fieldNameFilter = new Filter<Field>("name", (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.like(criteriaBuilder.lower(path.as(String.class)), filterInput + "%");
        });

        fieldDataProvider.addFilter(fieldFilter);
        fieldDataProvider.addFilter(fieldNameFilter);

        fieldDataProvider.callFilters("name", SortOrder.ASCENDING);

        mapContainer = new InstitutionFieldMapContainer();
        for(InstitutionField institutionField : institutionFieldService.findAllByInstitution_Id(item.getId()))
            ((InstitutionFieldContainer) mapContainer.addContainer(institutionField.getField(), institutionField, ContainerState.SHOW))
                    .setGridItem(true);

        institutionFieldGrid = new InstitutionFieldGrid();
        institutionFieldList = new InstitutionFieldList();

        updateView();

        patternMapContainers = new HashMap<>();
        multiplierMapContainers = new HashMap<>();
        allergenMapContainers = new HashMap<>();

        this.add(institutionFieldGrid, institutionFieldList);
        this.setHeight("30rem");
        this.getStyle()
                .setMarginTop("var(--lumo-space-m)");
    }

    @Getter
    private class InstitutionFieldGrid extends Grid<InstitutionFieldContainer>
    {
        private final TextField nameFilter;

        public InstitutionFieldGrid()
        {
            DropTarget<Grid<InstitutionFieldContainer>> dropTarget = DropTarget.create(this);
            dropTarget.addDropListener(event ->
            {
                event.getDragData().ifPresent(o ->
                {
                    if(!(o instanceof Field field)) return;

                    InstitutionFieldContainer container = (InstitutionFieldContainer) mapContainer.addIfAbsent(field, () ->
                    {
                        InstitutionField institutionField = new InstitutionField();
                        institutionField.setInstitution(item);
                        institutionField.setField(field);

                        return institutionField;
                    }, ContainerState.NEW);

                    container.setTempState(ContainerState.UPDATE);
                    container.setGridItem(true);

                    updateView();
                });
            });

            this.addComponentColumn(container ->
            {
                SvgIcon trash = LineAwesomeIcon.TRASH_SOLID.create();
                trash.addClickListener(event ->
                {
                    if(container.getState() == ContainerState.NEW)
                    {
                        container.setTempState(ContainerState.HIDE);
                    }else container.setTempState(ContainerState.DELETE);

                    container.setGridItem(false);
                    updateView();
                });

                return trash;

            }).setHeader(LineAwesomeIcon.TRASH_SOLID.create())
                    .setWidth("50px").setAutoWidth(false).setFlexGrow(0);

            nameFilter = new TextField();
            nameFilter.setWidth("99%");
            nameFilter.setPlaceholder("Name...");
            nameFilter.setValueChangeMode(ValueChangeMode.EAGER);

            this.addColumn(container ->
            {
                return container.getEntity().getField().getName();
            }).setHeader(nameFilter)
                    .setSortable(true)
                    .setComparator(Comparator.comparing(container -> container.getEntity().getField().getName()));

            nameFilter.addValueChangeListener(event ->
            {
                this.setItems(
                        getItems().get(true)
                                .stream()
                                .filter(container ->
                                {
                                    return container.getEntity().getField().getName().toLowerCase().startsWith(event.getValue().toLowerCase());
                                }).collect(Collectors.toList())
                );
            });

            this.addComponentColumn(container ->
            {
                SvgIcon cog = LineAwesomeIcon.COG_SOLID.create();
                cog.addClickListener(event ->
                {
                   this.openInstitutionFieldDialog(container);
                });

                return cog;
            }).setHeader(LineAwesomeIcon.COG_SOLID.create())
                    .setWidth("50px").setAutoWidth(false).setFlexGrow(0);

            this.addItemDoubleClickListener(event ->
            {
                this.openInstitutionFieldDialog(event.getItem());
            });

            this.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
            this.setWidth("99%");
            this.setHeightFull();
        }

        public InstitutionFieldGrid updateFilters()
        {
            this.setItems(
                    getItems().get(true)
                            .stream()
                            .filter(container ->
                            {
                                return container.getEntity().getField().getName().toLowerCase().startsWith(nameFilter.getValue().toLowerCase());
                            }).collect(Collectors.toList())
            );

            return this;
        }

        public InstitutionFieldGrid openInstitutionFieldDialog(InstitutionFieldContainer container)
        {
            Field mapper = container.getEntity().getField();

            PatternMapContainer patternMapContainer = patternMapContainers
                    .computeIfAbsent(mapper, field ->
                    {
                        PatternMapContainer mapContainer = new PatternMapContainer();

                        for(InstitutionPattern institutionPattern : container.getEntity().getInstitutionPatterns())
                            mapContainer.addContainer(institutionPattern.getPattern(), institutionPattern, ContainerState.SHOW);

                        return mapContainer;
                    });

            MultiplierMapContainer multiplierMapContainer = multiplierMapContainers
                    .computeIfAbsent(mapper, field ->
                    {
                        MultiplierMapContainer mapContainer = new MultiplierMapContainer();

                        for(InstitutionMultiplier institutionMultiplier : container.getEntity().getInstitutionMultipliers())
                            mapContainer.addContainer(institutionMultiplier.getCourse(), institutionMultiplier, ContainerState.SHOW);

                        return mapContainer;
                    });

            AllergenMapContainer allergenMapContainer = allergenMapContainers
                    .computeIfAbsent(mapper, field ->
                    {
                        AllergenMapContainer mapContainer = new AllergenMapContainer();

                        for(InstitutionAllergen institutionAllergen : container.getEntity().getInstitutionAllergens())
                            mapContainer.addContainer(mapContainer.nextMapper(), institutionAllergen, ContainerState.SHOW);

                        return mapContainer;
                    });

            new InstitutionFieldDialog(patternService, fieldMultiplierService, courseService, allergenService, container.getEntity(), patternMapContainer, multiplierMapContainer, allergenMapContainer);

            return this;
        }

    }

    @Getter
    private class InstitutionFieldList extends VerticalLayout
    {
        private final VirtualList<Field> virtualList;
        private final TextField filterField;

        public InstitutionFieldList()
        {
            virtualList = new VirtualList<>();
            virtualList.setDataProvider(fieldDataProvider.getFilterDataProvider());
            virtualList.setRenderer(new ComponentRenderer<>(field ->
            {
                TextField showField = new TextField();
                showField.setWidth("99%");
                showField.setValue(field.getName());
                showField.setReadOnly(true);

                DragSource dragSource = DragSource.create(showField);
                dragSource.addDragStartListener(event ->
                {
                    dragSource.setDragData(field);
                });

                return showField;
            }));

            filterField = new TextField();
            filterField.setPlaceholder("Suchen...");
            filterField.setValueChangeMode(ValueChangeMode.EAGER);
            filterField.getStyle()
                    .setWidth("99%")
                    .setPaddingTop("0px");

            filterField.addValueChangeListener(event ->
            {
                fieldNameFilter.setFilterInput(event.getValue());
                fieldDataProvider.refreshAll();
            });

            this.add(filterField, virtualList);
            this.setWidth("99%");
            this.setHeightFull();
            this.setPadding(false);
        }


    }

    public Map<Boolean, List<InstitutionFieldContainer>> getItems()
    {
        return mapContainer.getContainers()
                .stream()
                .map(container -> (InstitutionFieldContainer) container)
                .collect(Collectors.partitioningBy(InstitutionFieldContainer::isGridItem));
    }

    public InstitutionFieldComponent updateView()
    {
        Map<Boolean, List<InstitutionFieldContainer>> items = getItems();

        institutionFieldGrid.setItems(
                items.get(true)
                        .stream()
                        .sorted(Comparator.comparing(container -> container.getEntity().getField().getName()))
                        .collect(Collectors.toList())
        );

        institutionFieldGrid.updateFilters();
        fieldDataProvider.refreshAll();

        return this;
    }
}
