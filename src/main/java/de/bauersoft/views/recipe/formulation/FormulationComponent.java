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
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.formulation.Formulation;
import de.bauersoft.data.entities.formulation.FormulationKey;
import de.bauersoft.data.entities.ingredient.Ingredient;
import de.bauersoft.data.entities.recipe.Recipe;
import de.bauersoft.services.FormulationService;
import de.bauersoft.services.IngredientService;
import de.bauersoft.services.RecipeService;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class FormulationComponent extends HorizontalLayout
{

    private final Recipe item;

    private final RecipeService recipeService;
    private final FormulationService formulationService;
    private final IngredientService ingredientService;

    private final FormulationMapContainer mapContainer;

    private final FormulationGrid formulationGrid;
    private final FormulationList formulationList;

    public FormulationComponent(Recipe item, RecipeService recipeService, FormulationService formulationService, IngredientService ingredientService)
    {
        this.item = item;
        this.recipeService = recipeService;
        this.formulationService = formulationService;
        this.ingredientService = ingredientService;

        mapContainer = new FormulationMapContainer();
        for(Formulation formulation : formulationService.findAllByRecipe_Id(item.getId()))
            ((FormulationContainer) mapContainer.addContainer(formulation.getIngredient(), formulation, ContainerState.SHOW))
                    .setGridItem(true);

        for(Ingredient ingredient : ingredientService.findAll())
        {
            mapContainer.addIfAbsent(ingredient, () ->
            {
                Formulation formulation = new Formulation();
                formulation.setId(new FormulationKey(null, ingredient.getId()));
                formulation.setRecipe(item);
                formulation.setIngredient(ingredient);

                return formulation;

            }, ContainerState.NEW);
        }

        formulationGrid = new FormulationGrid();
        formulationList = new FormulationList();

        updateView();

        this.add(formulationGrid, formulationList);
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
                    if(!(o instanceof FormulationContainer container)) return;

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
            }).setHeader(LineAwesomeIcon.TRASH_SOLID.create()).setWidth("50px").setAutoWidth(false).setFlexGrow(0);

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
    private class FormulationList extends VerticalLayout
    {
        private final VirtualList<FormulationContainer> virtualList;
        private final TextField filterField;

        public FormulationList()
        {
            virtualList = new VirtualList<>();
            virtualList.setRenderer(new ComponentRenderer<>(container ->
            {
                TextField showField = new TextField();
                showField.setWidth("99%");
                showField.setValue(container.getEntity().getIngredient().getName());
                showField.setReadOnly(true);

                DragSource dragSource = DragSource.create(showField);
                dragSource.addDragStartListener(event ->
                {
                    dragSource.setDragData(container);
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
                virtualList.setItems(
                        getItems().get(false)
                                .stream()
                                .filter(container -> container.getEntity().getIngredient().getName().toLowerCase().startsWith(event.getValue().toLowerCase()))
                                .collect(Collectors.toList())
                );
            });

            this.add(filterField, virtualList);
            this.setWidth("59%");
            this.setHeightFull();
            this.setPadding(false);
        }

        public FormulationList updateFilter()
        {
            String value = filterField.getValue();
            filterField.setValue("");
            filterField.setValue(Objects.requireNonNullElse(value, ""));

            return this;
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

        formulationList.getVirtualList().setItems(items.get(false));
        formulationList.updateFilter();

        return this;
    }
}
