package de.bauersoft.components.autofilter.grid;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Direction;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.ColumnRendering;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import de.bauersoft.components.autofilter.Filter;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.unit.Unit;
import de.bauersoft.services.ServiceBase;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Sort;

import java.util.function.Consumer;

public class AutofilterGrid<T, ID> extends Grid<T>
{
    private FilterDataProvider<T, ID> dataProvider;
    private ServiceBase<T, ID> service;

    private HeaderRow headerRow;

    private final AutofilterGridContextMenu autofilterGridContextMenu;

    public AutofilterGrid(FilterDataProvider<T, ID> dataProvider)
    {
        this.dataProvider = dataProvider;
        this.service = dataProvider.getService();

        this.getHeaderRows().clear();
        headerRow = appendHeaderRow();

        autofilterGridContextMenu = new AutofilterGridContextMenu();

        this.setDataProvider(dataProvider.getFilterDataProvider());

        this.setColumnRendering(ColumnRendering.LAZY);
    }

    public Grid.Column<T> addColumn(String attributeName, String header, ValueProvider<T, String> valueProvider, boolean caseSensitive)
    {
        return addColumn(attributeName, header, valueProvider, s -> s.toLowerCase() + "%", caseSensitive);
    }

    public Grid.Column<T> addColumn(String attributeName, String header, ValueProvider<T, String> valueProvider, ValueProvider<String, String> patternProvider, boolean caseSensitive)
    {
        return addColumn(attributeName, header, valueProvider, (root, path, criteriaQuery, criteriaBuilder, parent, filter) ->
        {
            return (caseSensitive) ?
                    criteriaBuilder.like(path.as(String.class), patternProvider.apply(filter)) :
                    criteriaBuilder.like(criteriaBuilder.lower(path.as(String.class)), patternProvider.apply(filter).toLowerCase());
        });
    }

    public Grid.Column<T> addColumn(String attributeName, String header, ValueProvider<T, String> valueProvider, Filter.FilterFunction<T> filterFunction)
    {
        Grid.Column<T> column = this.addColumn(valueProvider);
        column.setResizable(true);

        Filter<T> filter = new Filter<>(attributeName, filterFunction);

        dataProvider.getFilters().add(filter);

//        column.setHeader(createFilterHeader(header, value ->
//        {
//            filter.setFilterInput(value);
//            dataProvider.callFilters();
//        }));

        headerRow.getCell(column).setComponent(createFilterHeader(header, value ->
        {
            filter.setFilterInput(value);
            dataProvider.callFilters();
        }));

//        HeaderRow.HeaderCell headerCell = headerRow.getCell(column);
//        headerCell.setComponent(createFilterHeader(header, value ->
//        {
//            filter.setFilterInput(value);
//            dataProvider.callFilters();
//        }));


        return column;
    }

//    public Grid.Column<T> addColumn(String header, ValueProvider<T, String> valueProvider,ValueProvider<Root<?>, Path<?>> pathProvider, Filter.FilterFunction<T> filterFunction)
//    {
//        Grid.Column<T> column = this.addColumn(valueProvider);
//        column.setResizable(true);
//
//        Filter<T> filter = new Filter<>(pathProvider, filterFunction);
//        filter.setFilterFunction(filterFunction);
//
//        this.addFilter(filter);
//
//        headerRow.getCell(column).setComponent(createFilterHeader(header, value ->
//        {
//            filter.setFilterInput(value);
//            dataProvider.callFilters();
//        }));
//
//        return column;
//    }

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

    private VerticalLayout createFilterHeader(String header, Consumer<String> onFilterChangeConsumer)
    {
        NativeLabel label = new NativeLabel((header == null || header.isEmpty()) ? "\u200B" : header);
        label.getStyle()
                .set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-m)");

        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.setWidthFull();
        textField.getStyle().set("max-width", "100%");
        textField.addValueChangeListener(event ->
        {
            onFilterChangeConsumer.accept(event.getValue());
        });

        VerticalLayout layout = new VerticalLayout(label, textField);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }

    public AutofilterGrid<T, ID> addFilter(Filter<T> filter)
    {
        dataProvider.addFilter(filter);
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
