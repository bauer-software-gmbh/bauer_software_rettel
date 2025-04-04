package de.bauersoft.views.recipe.formulation;

import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import de.bauersoft.components.autofilter.Filter;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.formulation.Formulation;
import de.bauersoft.data.entities.formulation.FormulationKey;
import de.bauersoft.data.entities.ingredient.Ingredient;
import de.bauersoft.data.entities.recipe.Recipe;
import de.bauersoft.services.FormulationService;
import de.bauersoft.services.IngredientService;
import de.bauersoft.services.RecipeService;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class FormulationComponent extends HorizontalLayout
{
    private final Recipe item;

    private final RecipeService recipeService;
    private final FormulationService formulationService;
    private final IngredientService ingredientService;

    private final FilterDataProvider<Ingredient, Long> ingredientDataProvider;
    private Filter<Ingredient> ingredientFilter;
    private Filter<Ingredient> ingredientNameFilter;

    private final FormulationMapContainer mapContainer;

    private final FormulationGrid formulationGrid;
    private final IngredientGrid ingredientGrid;

    public FormulationComponent(Recipe item, RecipeService recipeService, FormulationService formulationService, IngredientService ingredientService)
    {
        this.item = item;
        this.recipeService = recipeService;
        this.formulationService = formulationService;
        this.ingredientService = ingredientService;

        ingredientDataProvider = new FilterDataProvider<>(ingredientService);
        ingredientFilter = new Filter<Ingredient>("name", (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            List<Ingredient> doNotShow = new ArrayList<>(
                    getItems().get(true)
                            .stream()
                            .map(container -> container.getEntity().getIngredient())
                            .collect(Collectors.toList())
            );

            if(!doNotShow.isEmpty())
            {
                CriteriaBuilder.In<Long> inClause = criteriaBuilder.in(root.get("id"));
                for(Ingredient ingredient : doNotShow)
                    inClause.value(ingredient.getId());

                return criteriaBuilder.not(inClause);
            }

            return criteriaBuilder.conjunction();

        }).setIgnoreFilterInput(true);

        ingredientNameFilter = new Filter<Ingredient>("name", (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.like(criteriaBuilder.lower(path.as(String.class)), filterInput.toLowerCase() + "%");
        });

        ingredientDataProvider.addFilter(ingredientFilter);
        ingredientDataProvider.addFilter(ingredientNameFilter);

        ingredientDataProvider.applyFilters("name", SortOrder.ASCENDING);

        mapContainer = new FormulationMapContainer();
        for(Formulation formulation : formulationService.findAllByRecipe_Id(item.getId()))
            ((FormulationContainer) mapContainer.addContainer(formulation.getIngredient(), formulation, ContainerState.SHOW))
                    .setGridItem(true);

        formulationGrid = new FormulationGrid();
        ingredientGrid = new IngredientGrid();

        updateView();

        this.add(formulationGrid, ingredientGrid);
        this.setHeight("30rem");
        this.getStyle()
                .setMarginTop("var(--lumo-space-m)");
    }

    @Getter
    private class FormulationGrid extends Grid<FormulationContainer>
    {
        private final TextField nameFilter;
        private final NumberField quantityFilter;
        private final TextField unitFilter;

        public FormulationGrid()
        {
            DropTarget<Grid<FormulationContainer>> dropTarget = DropTarget.create(this);
            dropTarget.addDropListener(event ->
            {
                event.getDragData().ifPresent(o ->
                {
                    if(!(o instanceof Ingredient ingredient)) return;

                    FormulationContainer container = (FormulationContainer) mapContainer.addIfAbsent(ingredient, () ->
                    {
                        Formulation formulation = new Formulation();
                        formulation.setId(new FormulationKey(null, ingredient.getId()));
                        formulation.setRecipe(item);
                        formulation.setIngredient(ingredient);

                        return formulation;
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
                        return container.getEntity().getIngredient().getName();
                    }).setHeader(nameFilter)
                    .setSortable(true)
                    .setComparator(container -> container.getEntity().getIngredient().getName());

            quantityFilter = new NumberField();
            quantityFilter.setWidth("99%");
            quantityFilter.setAllowedCharPattern("[0-9,.]");
            quantityFilter.setPlaceholder("Anzahl...");
            quantityFilter.setValueChangeMode(ValueChangeMode.EAGER);

            this.addColumn(new ComponentRenderer<>(container ->
                    {
                        NumberField numberField = new NumberField();
                        numberField.setWidth("99%");
                        numberField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
                        numberField.setAllowedCharPattern("[0-9,.]");

                        numberField.setValue(container.getTempQuantity());

                        numberField.addValueChangeListener(event ->
                        {
                            container.setTempQuantity(Objects.requireNonNullElse(event.getValue(), 0d));
                            container.setTempState(ContainerState.UPDATE);

                            updateView();
                        });

                        return numberField;

                    })).setHeader(quantityFilter)
                    .setSortable(true)
                    .setComparator(Comparator.comparing(FormulationContainer::getTempQuantity));

            unitFilter = new TextField();
            unitFilter.setWidth("99%");
            unitFilter.setPlaceholder("Einheit...");
            unitFilter.setValueChangeMode(ValueChangeMode.EAGER);

            this.addColumn(container ->
                    {
                        return container.getEntity().getIngredient().getUnit().getName();
                    }).setHeader(unitFilter)
                    .setSortable(true)
                    .setComparator(Comparator.comparing(container -> container.getEntity().getIngredient().getUnit().getName()));

            nameFilter.addValueChangeListener(event ->
            {
                this.setItems(
                        getItems().get(true)
                                .stream()
                                .filter(container ->
                                {
                                    Formulation entity = container.getEntity();
                                    return entity.getIngredient().getName().toLowerCase().startsWith(event.getValue().toLowerCase()) &&
                                            entity.getIngredient().getUnit().getName().toLowerCase().startsWith(unitFilter.getValue().toLowerCase()) &&
                                            String.valueOf(container.getTempQuantity()).startsWith((quantityFilter.getValue() == null) ? "" : String.valueOf(quantityFilter.getValue()));

                                }).collect(Collectors.toList())
                );
            });

            quantityFilter.addValueChangeListener(event ->
            {
                this.setItems(
                        getItems().get(true)
                                .stream()
                                .filter(container ->
                                {
                                    Formulation entity = container.getEntity();
                                    return entity.getIngredient().getName().toLowerCase().startsWith(nameFilter.getValue().toLowerCase()) &&
                                            entity.getIngredient().getUnit().getName().toLowerCase().startsWith(unitFilter.getValue().toLowerCase()) &&
                                            String.valueOf(container.getTempQuantity()).startsWith((event.getValue() == null) ? "" : String.valueOf(event.getValue()));

                                }).collect(Collectors.toList())
                );
            });

            unitFilter.addValueChangeListener(event ->
            {
                this.setItems(
                        getItems().get(true)
                                .stream()
                                .filter(container ->
                                {
                                    Formulation entity = container.getEntity();
                                    return entity.getIngredient().getName().toLowerCase().startsWith(nameFilter.getValue().toLowerCase()) &&
                                            entity.getIngredient().getUnit().getName().toLowerCase().startsWith(event.getValue().toLowerCase()) &&
                                            String.valueOf(container.getTempQuantity()).startsWith((quantityFilter.getValue() == null) ? "" : String.valueOf(quantityFilter.getValue()));

                                }).collect(Collectors.toList())
                );
            });

            this.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
            this.setWidth("99%");
            this.setHeightFull();
        }

        public FormulationGrid updateFilters()
        {
            this.setItems(
                    getItems().get(true)
                            .stream()
                            .filter(container ->
                            {
                                Formulation entity = container.getEntity();
                                return entity.getIngredient().getName().toLowerCase().startsWith(nameFilter.getValue().toLowerCase()) &&
                                        entity.getIngredient().getUnit().getName().toLowerCase().startsWith(unitFilter.getValue().toLowerCase()) &&
                                        String.valueOf(container.getTempQuantity()).startsWith((quantityFilter.getValue() == null) ? "" : String.valueOf(quantityFilter.getValue()));

                            }).collect(Collectors.toList())
            );

            return this;
        }
    }

    @Getter
    private class IngredientGrid extends VerticalLayout
    {
        private final TextField filterField;
        private final Grid<Ingredient> ingredientGrid;

        public IngredientGrid()
        {
            filterField = new TextField();
            ingredientGrid = new Grid<>();

            filterField.setPlaceholder("Suchen...");
            filterField.setValueChangeMode(ValueChangeMode.LAZY);
            filterField.getStyle()
                    .setWidth("99%")
                    .setPaddingTop("0px");

            filterField.addValueChangeListener(event ->
            {
                ingredientNameFilter.setFilterInput(event.getValue());
                ingredientDataProvider.refreshAll();
            });

            ingredientGrid.setDataProvider(ingredientDataProvider.getFilterDataProvider());
            ingredientGrid.addColumn(new ComponentRenderer<>(ingredient ->
            {
                TextField showField = new TextField();
                showField.setWidth("99%");
                showField.setValue(ingredient.getName());
                showField.setReadOnly(true);

                DragSource dragSource = DragSource.create(showField);
                dragSource.addDragStartListener(event ->
                {
                    dragSource.setDragData(ingredient);
                });

                return showField;
            })).setHeader(filterField);

            this.add(ingredientGrid);
            this.setWidth("59%");
            this.setHeightFull();
            this.setPadding(false);
        }
    }

    public Map<Boolean, List<FormulationContainer>> getItems()
    {
        return mapContainer.getContainers()
                .stream()
                .map(container -> (FormulationContainer) container)
                .collect(Collectors.partitioningBy(FormulationContainer::isGridItem));
    }

    public FormulationComponent updateView()
    {
        Map<Boolean, List<FormulationContainer>> items = getItems();

        formulationGrid.setItems(
                items.get(true)
                        .stream()
                        .sorted(Comparator.comparing(container -> container.getEntity().getIngredient().getName()))
                        .collect(Collectors.toList())
        );

        formulationGrid.updateFilters();
        ingredientDataProvider.refreshAll();

        return this;
    }
}
