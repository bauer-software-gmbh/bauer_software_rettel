package de.bauersoft.components;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.HeaderRow.HeaderCell;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;

import de.bauersoft.data.entities.AbstractGroupByEntity;
import de.bauersoft.data.providers.experimental.HasGroupBy;

@CssImport(value = "./themes/rettels/components/group-by-grid.css")
public class GroupByTreeGrid<T extends AbstractGroupByEntity<T>> extends TreeGrid<T> {
	private HeaderRow headerRow;
	private List<Column<T>> baseColumns = new ArrayList<Column<T>>();
	private List<String> groupByFieldList = new ArrayList<String>();
	private Column<T> groupColumn;

	public GroupByTreeGrid(Class<T> beanType) {
		super(beanType);
		this.removeAllColumns();
		groupColumn = this.addHierarchyColumn(item -> item.getValue()).setKey("generated-group-by").setHeader("Group")
				.setAutoWidth(true).setResizable(true).setFlexGrow(0);
		
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		if (this.getDataProvider() instanceof HasGroupBy dataProvider) {
			if (!this.getHeaderRows().isEmpty()) {
				baseColumns = new ArrayList<Column<T>>(getColumns());
				for (Column<T> column : getColumns()) {
					HeaderCell headerCell = this.getHeaderRows().get(0).getCell(column);
					if (headerCell != null && !"generated-group-by".equals(column.getKey())) {
						headerCell.setComponent(headerCell.getComponent() != null
								? new GroupByHeader(column.getKey(), headerCell.getComponent())
								: new GroupByHeader(column.getKey(), new Span(headerCell.getText())));
					}
				}
			}
			if (headerRow == null) {
				headerRow = this.prependHeaderRow();
				HeaderCell cell = headerRow.join(headerRow.getCells());
				HorizontalLayout groupByDropLayout = new HorizontalLayout();
				groupByDropLayout.add(LineAwesomeIcon.TASKS_SOLID.create());
				groupByDropLayout.setClassName("groupby-header");
				groupByDropLayout.setWidthFull();
				groupByDropLayout.getElement().setAttribute("empty", groupByFieldList.isEmpty());
				DropTarget.create(groupByDropLayout).addDropListener(dropEvent -> {
					dropEvent.getDragSourceComponent().ifPresent(dragSource -> {
						if (dragSource instanceof GroupByHeader header) {
							Span badge = new Span(header.getGroupBy());
							badge.getStyle().setPaddingRight(".5rem");
							SvgIcon close = LineAwesomeIcon.WINDOW_CLOSE.create();
							close.getStyle().setMarginLeft(".5rem");
							badge.add(close);
							close.addClickListener(clickEvent -> {
								groupByFieldList.remove(header.getGroupBy());
								groupByDropLayout.remove(badge);
								setColumns();
								dataProvider.setGroupeBy(groupByFieldList);
								groupByDropLayout.getElement().setAttribute("empty", groupByFieldList.isEmpty());
							});
							badge.getElement().getThemeList().add("badge contrast pill");
							groupByDropLayout.add(badge);
							this.getColumns();
							groupByFieldList.add(header.getGroupBy());
							setColumns();
							dataProvider.setGroupeBy(groupByFieldList);
							groupByDropLayout.getElement().setAttribute("empty", groupByFieldList.isEmpty());
						}
					});
				});
				cell.setComponent(groupByDropLayout);
			}
		}
		this.groupColumn.setVisible(!groupByFieldList.isEmpty());
		super.recalculateColumnWidths();
	}

	private void setColumns() {
		for (Column<T> column : this.baseColumns) {
			column.setVisible(("generated-group-by".equals(column.getKey()) && !groupByFieldList.isEmpty()) || (!"generated-group-by".equals(column.getKey()) && !groupByFieldList.contains(column.getKey())));		
		}
		super.recalculateColumnWidths();
	}
}
