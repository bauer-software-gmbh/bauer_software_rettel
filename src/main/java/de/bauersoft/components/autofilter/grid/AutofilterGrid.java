package de.bauersoft.components.autofilter.grid;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.ColumnRendering;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import de.bauersoft.components.autofilter.Filter;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.services.ServiceBase;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import javax.swing.SortOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@CssImport(value = "./themes/rettels/components/auto-filter-grid.css")
public class AutofilterGrid<T, ID> extends Grid<T>
{
    private FilterDataProvider<T, ID> dataProvider;
    private ServiceBase<T, ID> service;

    private final List<Column> columns;

    private HeaderRow headerRow;

    private Column sortColumn;

    private final AutofilterGridContextMenu autofilterGridContextMenu;

    public AutofilterGrid(FilterDataProvider<T, ID> dataProvider)
    {
        this.dataProvider = dataProvider;
        this.service = dataProvider.getService();

        columns = new ArrayList<>();

        this.getHeaderRows().clear();
        headerRow = appendHeaderRow();

        autofilterGridContextMenu = new AutofilterGridContextMenu();

        this.setDataProvider(dataProvider.getFilterDataProvider());

        this.setColumnRendering(ColumnRendering.LAZY);

        this.setClassName("autofilter-grid");
    }

    public Column addColumn(String attributeName, String header, ValueProvider<T, String> valueProvider)
    {
        return addColumn(attributeName, header, valueProvider, false);
    }

    public Column addColumn(String attributeName, String header, ValueProvider<T, String> valueProvider, boolean caseSensitive)
    {
        return addColumn(attributeName, header, valueProvider, caseSensitive, SortType.ALPHA);
    }

    public Column addColumn(String attributeName, String header, ValueProvider<T, String> valueProvider, boolean caseSensitive, SortType sortType)
    {
        return addColumn(attributeName, header, valueProvider, Filter.getDefaultFilterFunction(s -> "%" + s + "%", caseSensitive), Filter.getDefaultSortFunction(), sortType);
    }


    public Column addColumn(String attributeName, String header, ValueProvider<T, String> valueProvider, ValueProvider<String, String> patternProvider)
    {
        return addColumn(attributeName, header, valueProvider, patternProvider, false);
    }

    public Column addColumn(String attributeName, String header, ValueProvider<T, String> valueProvider, ValueProvider<String, String> patternProvider, boolean caseSensitive)
    {
        return addColumn(attributeName, header, valueProvider, patternProvider, caseSensitive, SortType.ALPHA);
    }

    public Column addColumn(String attributeName, String header, ValueProvider<T, String> valueProvider, ValueProvider<String, String> patternProvider, boolean caseSensitive, SortType sortType)
    {
        return addColumn(attributeName, header, valueProvider, Filter.getDefaultFilterFunction(patternProvider, caseSensitive), Filter.getDefaultSortFunction(), sortType);
    }

    public Column addColumn(String attributeName, String header, ValueProvider<T, String> valueProvider, Filter.FilterFunction<T> filterFunction)
    {
        return addColumn(attributeName, header, valueProvider, filterFunction, SortType.ALPHA);
    }

    public Column addColumn(String attributeName, String header, ValueProvider<T, String> valueProvider, Filter.FilterFunction<T> filterFunction, SortType sortType)
    {
        return addColumn(attributeName, header, valueProvider, filterFunction, Filter.getDefaultSortFunction(), sortType);
    }

    public Column addColumn(String attributeName, String header, ValueProvider<T, String> valueProvider, Filter.FilterFunction<T> filterFunction, Filter.SortFunction<T> sortFunction, SortType sortType)
    {
        Filter<T> filter = new Filter<>(attributeName, filterFunction, sortFunction);
        addFilter(filter);

        Column column = new Column(attributeName, sortType, header, valueProvider, s ->
        {
            filter.setFilterInput(s);
            if(sortColumn == null)
                dataProvider.callFilters();
            else
                dataProvider.callFilters(sortColumn.getAttributeName(), sortColumn.getSortOrder());
        });

        columns.add(column);

        setActiveSortColumn(columns.get(0), SortOrder.ASCENDING);

        return column;
    }

    @Override
    public <V extends Component> Grid.Column<T> addComponentColumn(ValueProvider<T, V> componentProvider)
    {
        return addComponentColumn(null, null, componentProvider);
    }

    public <V extends Component> Grid.Column<T> addComponentColumn(String header, String width, ValueProvider<T, V> componentProvider)
    {
        Grid.Column<T> column = super.addComponentColumn(componentProvider).setWidth(width).setFlexGrow(0).setAutoWidth(true);

        NativeLabel label = new NativeLabel(header);
        label.getStyle()
                .set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-m)");

        TextField textField = new TextField();
        textField.setWidth(width);
        textField.setReadOnly(true);

        VerticalLayout layout = new VerticalLayout(label, textField);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        headerRow.getCell(column).setComponent(layout);

        return column;
    }

//    private VerticalLayout createFilterHeader(String header, Consumer<String> onFilterChangeConsumer)
//    {
//        NativeLabel label = new NativeLabel((header == null || header.isEmpty()) ? "\u200B" : header);
//        label.getStyle()
//                .set("padding-top", "var(--lumo-space-m)")
//                .set("font-size", "var(--lumo-font-size-m)");
//
//        TextField textField = new TextField();
//        textField.setValueChangeMode(ValueChangeMode.EAGER);
//        textField.setClearButtonVisible(true);
//        textField.setWidthFull();
//        textField.getStyle().set("max-width", "100%");
//        textField.addValueChangeListener(event ->
//        {
//            onFilterChangeConsumer.accept(event.getValue());
//        });
//
//        VerticalLayout layout = new VerticalLayout(label, textField);
//        layout.getThemeList().clear();
//        layout.getThemeList().add("spacing-xs");
//
//        return layout;
//    }

    public AutofilterGrid<T, ID> addFilter(Filter<T> filter)
    {
        dataProvider.addFilter(filter);
        return this;
    }

    @Getter
    public class Column extends VerticalLayout
    {
        private SortOrder sortOrder;
        private SortType sortType;
        private boolean enableSorting;

        private final String attributeName;
        private final String header;
        private final ValueProvider<T, String> valueProvider;
        private final Consumer<String> onFilterChangeConsumer;

        private final Grid.Column<T> gridColumn;

        private final NativeLabel label;
        private final HorizontalLayout inputLayout;
        private final TextField inputField;
        private final Button sortButton;

        public Column(String attributeName, SortType sortType, String header, ValueProvider<T, String> valueProvider, Consumer<String> onFilterChangeConsumer)
        {
            sortOrder = SortOrder.UNSORTED;
            this.sortType = sortType;
            enableSorting = true;

            this.attributeName = attributeName;
            this.header = header;
            this.valueProvider = valueProvider;
            this.onFilterChangeConsumer = onFilterChangeConsumer;

            gridColumn = AutofilterGrid.super.addColumn(valueProvider);
            gridColumn.setResizable(true);
            headerRow.getCell(gridColumn).setComponent(this);

            label = new NativeLabel((header == null || header.isEmpty()) ? "\u200B" : header);
            label.getStyle()
                    .set("padding-top", "var(--lumo-space-m)")
                    .set("font-size", "var(--lumo-font-size-m)");

            inputLayout = new HorizontalLayout();
            inputLayout.setWidthFull();
            inputLayout.setAlignItems(Alignment.CENTER);
            inputLayout.getStyle().set("gap", "var(--lumo-space-m)");

            inputField = new TextField();
            inputField.setValueChangeMode(ValueChangeMode.EAGER);
            inputField.setClearButtonVisible(true);
            inputField.setWidth("100%");
            inputField.setMinWidth("1%");
            inputField.addValueChangeListener(event ->
            {
                onFilterChangeConsumer.accept(event.getValue());
            });


            sortButton = new Button(LineAwesomeIcon.SORT_SOLID.create());
            //sortButton.setWidth("20%");
            sortButton.addClickListener(event ->
            {
                AutofilterGrid.this.sortColumn = this;

                setSortOrder(nextSortOrder());

                dataProvider.callFilters(attributeName, sortOrder);

                for(Column column : columns)
                {
                    if(column == this) continue;
                    column.setSortOrder(SortOrder.UNSORTED);
                }
            });

            inputLayout.add(inputField, sortButton);

            this.add(label, inputLayout);
            this.getThemeList().clear();
            this.getThemeList().add("spacing-xs");
            this.getStyle().setPadding("0px");
        }

        public Column enableSorting(boolean enableSorting)
        {
            if(enableSorting && !this.enableSorting)
            {
                inputLayout.add(sortButton);

            }else if(!enableSorting && this.enableSorting)
            {
                inputLayout.remove(sortButton);
            }

            this.enableSorting = enableSorting;
            return this;
        }

        public SortOrder setSortOrder(SortOrder sortOrder)
        {
            this.sortOrder = sortOrder;
            sortButton.setIcon(sortType.get(sortOrder).create());

            return sortOrder;
        }

        public SortOrder nextSortOrder()
        {
            return nextSortOrder(sortOrder);
        }

        public static SortOrder nextSortOrder(SortOrder currentSortOrder)
        {
            switch(currentSortOrder)
            {
                case ASCENDING:
                    return SortOrder.DESCENDING;

                case DESCENDING:
                    return SortOrder.UNSORTED;

                case UNSORTED:
                    return SortOrder.ASCENDING;
            }

            return SortOrder.UNSORTED;
        }

    }

    public AutofilterGrid<T, ID> setActiveSortColumn(Column sortColumn, SortOrder sortOrder)
    {
        this.sortColumn = sortColumn;

        sortColumn.setSortOrder(sortOrder);

        dataProvider.callFilters(sortColumn.getAttributeName(), sortColumn.getSortOrder());

        for(Column column : columns)
        {
            if(column == sortColumn) continue;
                column.setSortOrder(SortOrder.UNSORTED);
        }

        return this;
    }






    public ServiceBase<T, ID> getService()
    {
        return service;
    }


    public HeaderRow getHeaderRow()
    {
        return headerRow;
    }

    public AutofilterGridContextMenu AutofilterGridContextMenu()
    {
        return autofilterGridContextMenu;
    }

    public class AutofilterGridContextMenu
    {
        private GridContextMenu<T> gridContextMenu;

        private GridMenuItem<T> gridMenuAddItem;
        private GridMenuItem<T> gridMenuDeleteItem;

        private AutofilterGridContextMenu() {}

        public AutofilterGridContextMenu enableGridContextMenu()
        {
            gridContextMenu = AutofilterGrid.super.addContextMenu();
            return this;
        }

        public AutofilterGridContextMenu enableAddItem(String label, ComponentEventListener<GridContextMenu.GridContextMenuItemClickEvent<T>> clickListener)
        {
            if(gridContextMenu == null)
                enableGridContextMenu();

            if(gridMenuAddItem == null)
                gridMenuAddItem = gridContextMenu.addItem(label, clickListener);

            return this;
        }

        public AutofilterGridContextMenu enableDeleteItem(String label, ComponentEventListener<GridContextMenu.GridContextMenuItemClickEvent<T>> clickListener)
        {
            if(gridContextMenu == null)
                enableGridContextMenu();

            if(gridMenuDeleteItem == null)
            {
                gridMenuDeleteItem = gridContextMenu.addItem(label, clickListener);

                gridContextMenu.addGridContextMenuOpenedListener(event ->
                {
                    gridMenuDeleteItem.setVisible(event.getItem().isPresent());
                });
            }

            return this;
        }

        public GridContextMenu<T> getGridContextMenu()
        {
            return gridContextMenu;
        }

        public AutofilterGridContextMenu setGridContextMenu(GridContextMenu<T> gridContextMenu)
        {
            this.gridContextMenu = gridContextMenu;
            return this;
        }

        public GridMenuItem<T> getGridMenuAddItem()
        {
            return gridMenuAddItem;
        }

        public AutofilterGridContextMenu setGridMenuAddItem(GridMenuItem<T> gridMenuAddItem)
        {
            this.gridMenuAddItem = gridMenuAddItem;
            return this;
        }

        public GridMenuItem<T> getGridMenuDeleteItem()
        {
            return gridMenuDeleteItem;
        }

        public AutofilterGridContextMenu setGridMenuDeleteItem(GridMenuItem<T> gridMenuDeleteItem)
        {
            this.gridMenuDeleteItem = gridMenuDeleteItem;
            return this;
        }
    }

}
