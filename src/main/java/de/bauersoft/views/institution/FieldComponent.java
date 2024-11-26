package de.bauersoft.views.institution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import de.bauersoft.data.entities.Field;
import de.bauersoft.data.entities.InstitutionFields;
import de.bauersoft.data.entities.InstitutionFieldsKey;
import de.bauersoft.data.entities.Institution;

public class FieldComponent extends FlexLayout {
	
	private List<Field> fieldsList = new ArrayList<Field>();
	private Map<InstitutionFields,Integer> institutionMap = new HashMap<InstitutionFields,Integer>(); 
	
	private Grid<InstitutionFields> institutionFieldsGrid = new Grid<InstitutionFields>(InstitutionFields.class,false);
	private VirtualList<Field> fieldVirtualList = new VirtualList<Field>();
	
	public FieldComponent(Institution institutionItem) {
		
		institutionFieldsGrid.addColumn(item->item.getField().getName()).setHeader("Name");
		institutionFieldsGrid.addColumn(new ComponentRenderer<>(item->{
			IntegerField numberField = new IntegerField();
			numberField.setValue(item.getChildCount());
			numberField.addValueChangeListener(event->{
				institutionMap.put(item,event.getValue());
			});
			return numberField;
		})).setHeader("Quantity").setWidth("75px");
		institutionFieldsGrid.addComponentColumn(item->  {
			SvgIcon icon = LineAwesomeIcon.TRASH_SOLID.create();
			icon.addClickListener(event->{
				institutionMap.remove(item);
				this.fieldsList.add(item.getField());
				updateView();
			});
			return icon;
		}).setWidth("25px");;
		institutionFieldsGrid.setSizeFull();
		// InstitutionFieldsAutoFilterGrid.setDropMode(GridDropMode.ON_GRID);
		DropTarget.create(institutionFieldsGrid).addDropListener(event->{
			event.getDragData().ifPresent(data->{
					if(data instanceof Field Field) {
						InstitutionFields InstitutionFields = new InstitutionFields();
						InstitutionFields.setField(Field);
						InstitutionFields.setId(new InstitutionFieldsKey());
						InstitutionFields.getId().setFieldId(Field.getId());
						this.institutionMap.put(InstitutionFields,InstitutionFields.getChildCount());
						this.fieldsList.remove(Field);
						updateView();
					}
			});
		});
		fieldVirtualList.setRenderer(new ComponentRenderer<Span,Field>(item->{
			Span span = new Span(item.getName());
			span.addClassName("drag-item");
			DragSource.create(span).addDragStartListener(event->{
				event.setDragData(item);
			});
			return span;
		}));
		fieldVirtualList.setSizeFull();
		this.add(institutionFieldsGrid,fieldVirtualList);
		this.setWidthFull();
	}

	public void setItems(Collection<Field> collection) {
		fieldsList.clear();
		institutionMap.clear();
		fieldsList.addAll(collection);
		updateView();
	}
	
	public void setValue(Collection<InstitutionFields> collection) {
		institutionMap.clear();
		if(collection != null) {
			collection.forEach(item->institutionMap.put(item,item.getChildCount()));
			fieldsList.removeAll(institutionMap.keySet().stream().filter(item-> fieldsList.contains(item.getField())).map(item-> item.getField()).toList());
		}
		updateView();
	}

	private void updateView() {
		this.fieldVirtualList.setItems(fieldsList);
		this.institutionFieldsGrid.setItems(institutionMap.keySet());
	}
	
	public void accept(Institution institution) {
		institutionMap.forEach((item,value)->{
			item.getId().setInstitutionId(institution.getId());
			item.setChildCount(value);
		});; 
	}

	public Set<InstitutionFields> getInstitutionFields() {
		return institutionMap.keySet();
	}
}
