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
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.*;

@CssImport(value="./themes/rettels/components/drag-item.css")
public class FormulationComponentCopy extends FlexLayout {

	private List<Ingredient> ingredientList = new ArrayList<Ingredient>();
	private Map<Formulation,Float> formulationMap = new HashMap<Formulation,Float>();

	private Grid<Formulation> formulationAutoFilterGrid = new Grid<Formulation>(Formulation.class,false);
	private VirtualList<Ingredient> ingredientVirtualList = new VirtualList<Ingredient>();

	public FormulationComponentCopy(Recipe recipeItem) {
		
		formulationAutoFilterGrid.addColumn(item->item.getIngredient().getName()).setHeader("Name");
		formulationAutoFilterGrid.addColumn(new ComponentRenderer<>(item->{
			NumberField numberField = new NumberField();
			numberField.setValue(Float.valueOf(item.getQuantity()).doubleValue());
			numberField.addValueChangeListener(event->{
				formulationMap.put(item,event.getValue().floatValue());
			});
			return numberField;
		})).setHeader("Quantity").setWidth("75px");
		formulationAutoFilterGrid.addColumn(item->item.getIngredient().getUnit().getShorthand()).setHeader("Unit").setWidth("75px");;

		formulationAutoFilterGrid.addComponentColumn(item->  {
			SvgIcon icon = LineAwesomeIcon.TRASH_SOLID.create();
			icon.addClickListener(event->{
				formulationMap.remove(item);
				this.ingredientList.add(item.getIngredient());
				updateView();
			});
			return icon;
		}).setWidth("25px");;
		formulationAutoFilterGrid.setSizeFull();
		// formulationAutoFilterGrid.setDropMode(GridDropMode.ON_GRID);
		DropTarget.create(formulationAutoFilterGrid).addDropListener(event->{
			event.getDragData().ifPresent(data->{
					if(data instanceof Ingredient ingredient) {
						Formulation formulation = new Formulation();
						formulation.setIngredient(ingredient);
						formulation.setId(new FormulationKey());
						formulation.getId().setIngredientId(ingredient.getId());
						this.formulationMap.put(formulation,formulation.getQuantity());
						this.ingredientList.remove(ingredient);
						updateView();
					}
			});
		});
		ingredientVirtualList.setRenderer(new ComponentRenderer<Span,Ingredient>(item->{
			Span span = new Span(item.getName());
			span.addClassName("drag-item");
			DragSource.create(span).addDragStartListener(event->{
				event.setDragData(item);
			});
			return span;
		}));
		ingredientVirtualList.setSizeFull();
		this.add(formulationAutoFilterGrid,ingredientVirtualList);
		this.setWidthFull();
	}

	public void setItems(Collection<Ingredient> collection) {
		ingredientList.clear();
		formulationMap.clear();
		ingredientList.addAll(collection);
		updateView();
	}
	
	public void setValues(Collection<Formulation> collection) {
		formulationMap.clear();
		if(collection != null) {
			collection.forEach(item->formulationMap.put(item,item.getQuantity()));
			ingredientList.removeAll(formulationMap.keySet().stream().filter(item-> ingredientList.contains(item.getIngredient())).map(item-> item.getIngredient()).toList());
		}
		updateView();
	}

	private void updateView() {
		this.ingredientVirtualList.setItems(ingredientList);
		this.formulationAutoFilterGrid.setItems(formulationMap.keySet());
	}
	
	public void accept(Recipe recipeItem) {
		formulationMap.forEach((item,value)->{
			item.getId().setRecipeId(recipeItem.getId());
			item.setQuantity(value);
		});; 
	}

	public Set<Formulation> getFormulations() {
		return formulationMap.keySet();
	}
}
