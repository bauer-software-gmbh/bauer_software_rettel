package de.bauersoft.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.html.Div;

public class GroupByHeader extends Div{
	private final String groupBy;
	public GroupByHeader(String groupBy, Component component) {
		this.groupBy = groupBy;
		DragSource.create(this);	
		this.add(component);
	}
	public String getGroupBy() {
		return groupBy;
	}
}