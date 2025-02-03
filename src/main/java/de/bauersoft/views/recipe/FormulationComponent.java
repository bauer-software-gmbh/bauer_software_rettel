package de.bauersoft.views.recipe;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import de.bauersoft.data.entities.formulation.Formulation;
import de.bauersoft.data.entities.formulation.FormulationKey;
import de.bauersoft.data.entities.ingredient.Ingredient;
import de.bauersoft.data.entities.recipe.Recipe;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@CssImport(value = "./themes/rettels/components/drag-item.css")
public class FormulationComponent extends FlexLayout
{

    private final List<Ingredient> ingredients;
    private final Map<Formulation, Float> formulationsMap;

    private final Grid<Formulation> formulationGrid;
    private final VirtualList<Ingredient> ingredientVirtualList;

    private final Map<Formulation, NumberField> numberFieldMap;

    public FormulationComponent()
    {
        ingredients = new ArrayList<>();
        formulationsMap = new HashMap<>();

        numberFieldMap = new HashMap<>();

        formulationGrid = new Grid<>(Formulation.class, false);
        formulationGrid.setSizeFull();

        formulationGrid.getStyle().setMarginRight("5px");

        formulationGrid.addColumn(formulation ->
        {
           return formulation.getIngredient().getName();
        }).setHeader("Name");

        formulationGrid.addColumn(new ComponentRenderer<NumberField, Formulation>(formulation ->
        {
            NumberField numberField = new NumberField();
            numberField.setMin(0);
            numberField.setMax(Float.MAX_VALUE);
            numberField.setValue(
                    (formulationsMap.containsKey(formulation) ? Float.valueOf(formulationsMap.get(formulation)).doubleValue() : Float.valueOf(formulation.getQuantity()).doubleValue())
            );

            numberFieldMap.put(formulation, numberField);

            numberField.addValueChangeListener(event ->
            {
                formulationsMap.put(formulation, Double.valueOf(Objects.requireNonNullElse(event.getValue(), 0d)).floatValue());
            });

            return numberField;
        })).setHeader("Anzahl").setWidth("75px");

        formulationGrid.addColumn(formulation ->
        {
            return formulation.getIngredient().getUnit().getShorthand();
        }).setHeader("Einheit").setWidth("75px");

        formulationGrid.addComponentColumn(formulation ->
        {
            SvgIcon trashCan = LineAwesomeIcon.TRASH_SOLID.create();
            trashCan.addClickListener(event ->
            {
                numberFieldMap.remove(formulation);
                formulationsMap.remove(formulation);
                ingredients.add(formulation.getIngredient());
                updateView();
            });

            return trashCan;
        }).setWidth("25px");

        DropTarget.create(formulationGrid).addDropListener(event ->
        {
            event.getDragData().ifPresent(object ->
            {
                if(!(object instanceof Ingredient ingredient)) return;

                Formulation formulation = new Formulation();
                formulation.setIngredient(ingredient);
                formulation.setId(new FormulationKey());
                formulation.getId().setIngredientId(ingredient.getId());

                formulationsMap.put(formulation, formulation.getQuantity());
                ingredients.remove(ingredient);
                updateView();
            });
        });

        ingredientVirtualList = new VirtualList<>();
        ingredientVirtualList.setSizeFull();

        ingredientVirtualList.setRenderer(new ComponentRenderer<Span, Ingredient>(item ->
        {
            Span span = new Span(item.getName());
            span.addClassName("drag-item");

            DragSource.create(span).addDragStartListener(event ->
            {
                event.setDragData(item);
            });

            return span;
        }));

        this.add(formulationGrid, ingredientVirtualList);
        this.setWidthFull();
    }

    public void setFormulations(List<Formulation> formulations)
    {
        if(formulations == null) return;
        formulationsMap.clear();

        formulationsMap.putAll(formulations
                .stream()
                .collect(Collectors.toMap(
                        formulation -> formulation,
                        formulation -> formulation.getQuantity()
                )));
    }

    public void setIngredients(List<Ingredient> ingredients)
    {
        if(ingredients == null) return;
        this.ingredients.clear();

        this.ingredients.addAll(ingredients);
    }

    public void updateView()
    {
        formulationsMap.keySet().forEach(formulation -> ingredients.remove(formulation.getIngredient()));

        ingredientVirtualList.setItems(ingredients);
        formulationGrid.setItems(formulationsMap.keySet());
    }

    public void accept(Recipe recipe)
    {
        formulationsMap.forEach((formulation, quantity) ->
        {
            formulation.setQuantity(quantity.floatValue());
            formulation.setRecipe(recipe);
            formulation.getId().setRecipeId(recipe.getId());
            formulation.getId().setIngredientId(formulation.getIngredient().getId());
        });
    }

    public boolean isValid()
    {
        return numberFieldMap.values().stream().allMatch(numberField -> !numberField.isInvalid());
    }
}
