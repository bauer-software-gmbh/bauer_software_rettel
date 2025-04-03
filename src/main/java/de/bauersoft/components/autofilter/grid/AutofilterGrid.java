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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.Renderer;
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

        if(sortColumn == null)
            dataProvider.applyFilters();
        else
            dataProvider.applyFilters(sortColumn.getAttributeName(), sortColumn.getSortOrder());

        Column column = new Column(attributeName, sortType, header, valueProvider, s ->
        {
            filter.setFilterInput(s);

            dataProvider.refreshAll();
        });

        columns.add(column);

        setActiveSortColumn(columns.get(0), SortOrder.ASCENDING);

        return column;
    }



    /**
     * @deprecated Use {@link AutofilterGrid#addRendererColumn} instead and use a {@link com.vaadin.flow.data.renderer.ComponentRenderer} as {@link Renderer}.
     */
    @Deprecated
    public Column addComponentColumn(String attributeName, String header, ValueProvider<T, Component> componentProvider)
    {
        return addComponentColumn(attributeName, header, componentProvider, false);
    }

    /**
     * @deprecated Use {@link AutofilterGrid#addRendererColumn} instead and use a {@link com.vaadin.flow.data.renderer.ComponentRenderer} as {@link Renderer}.
     */
    @Deprecated
    public Column addComponentColumn(String attributeName, String header, ValueProvider<T, Component> componentProvider, boolean caseSensitive)
    {
        return addComponentColumn(attributeName, header, componentProvider, caseSensitive, SortType.ALPHA);
    }

    /**
     * @deprecated Use {@link AutofilterGrid#addRendererColumn} instead and use a {@link com.vaadin.flow.data.renderer.ComponentRenderer} as {@link Renderer}.
     */
    @Deprecated
    public Column addComponentColumn(String attributeName, String header, ValueProvider<T, Component> componentProvider, boolean caseSensitive, SortType sortType)
    {
        return addComponentColumn(attributeName, header, componentProvider, Filter.getDefaultFilterFunction(s -> "%" + s + "%", caseSensitive), Filter.getDefaultSortFunction(), sortType);
    }


    /**
     * @deprecated Use {@link AutofilterGrid#addRendererColumn} instead and use a {@link com.vaadin.flow.data.renderer.ComponentRenderer} as {@link Renderer}.
     */
    @Deprecated
    public Column addComponentColumn(String attributeName, String header, ValueProvider<T, Component> componentProvider, ValueProvider<String, String> patternProvider)
    {
        return addComponentColumn(attributeName, header, componentProvider, patternProvider, false);
    }

    /**
     * @deprecated Use {@link AutofilterGrid#addRendererColumn} instead and use a {@link com.vaadin.flow.data.renderer.ComponentRenderer} as {@link Renderer}.
     */
    @Deprecated
    public Column addComponentColumn(String attributeName, String header, ValueProvider<T, Component> componentProvider, ValueProvider<String, String> patternProvider, boolean caseSensitive)
    {
        return addComponentColumn(attributeName, header, componentProvider, patternProvider, caseSensitive, SortType.ALPHA);
    }

    /**
     * @deprecated Use {@link AutofilterGrid#addRendererColumn} instead and use a {@link com.vaadin.flow.data.renderer.ComponentRenderer} as {@link Renderer}.
     */
    @Deprecated
    public Column addComponentColumn(String attributeName, String header, ValueProvider<T, Component> componentProvider, ValueProvider<String, String> patternProvider, boolean caseSensitive, SortType sortType)
    {
        return addComponentColumn(attributeName, header, componentProvider, Filter.getDefaultFilterFunction(patternProvider, caseSensitive), Filter.getDefaultSortFunction(), sortType);
    }

    /**
     * @deprecated Use {@link AutofilterGrid#addRendererColumn} instead and use a {@link com.vaadin.flow.data.renderer.ComponentRenderer} as {@link Renderer}.
     */
    @Deprecated
    public Column addComponentColumn(String attributeName, String header, ValueProvider<T, Component> componentProvider, Filter.FilterFunction<T> filterFunction)
    {
        return addComponentColumn(attributeName, header, componentProvider, filterFunction, SortType.ALPHA);
    }

    /**
     * @deprecated Use {@link AutofilterGrid#addRendererColumn} instead and use a {@link com.vaadin.flow.data.renderer.ComponentRenderer} as {@link Renderer}.
     */
    @Deprecated
    public Column addComponentColumn(String attributeName, String header, ValueProvider<T, Component> componentProvider, Filter.FilterFunction<T> filterFunction, SortType sortType)
    {
        return addComponentColumn(attributeName, header, componentProvider, filterFunction, Filter.getDefaultSortFunction(), sortType);
    }

    /**
     * @deprecated Use {@link AutofilterGrid#addRendererColumn} instead and use a {@link com.vaadin.flow.data.renderer.ComponentRenderer} as {@link Renderer}.
     */
    @Deprecated
    public Column addComponentColumn(String attributeName, String header, ValueProvider<T, Component> componentProvider, Filter.FilterFunction<T> filterFunction, Filter.SortFunction<T> sortFunction, SortType sortType)
    {
        Filter<T> filter = new Filter<>(attributeName, filterFunction, sortFunction);
        addFilter(filter);

        Column column = new Column(attributeName, sortType, header, s ->
        {
            filter.setFilterInput(s);
            if(sortColumn == null)
                dataProvider.applyFilters();
            else
                dataProvider.applyFilters(sortColumn.getAttributeName(), sortColumn.getSortOrder());
        }, componentProvider);

        columns.add(column);

        setActiveSortColumn(columns.get(0), SortOrder.ASCENDING);

        return column;
    }



    public Column addRendererColumn(String attributeName, String header, Renderer<T> renderer)
    {
        return addRendererColumn(attributeName, header, renderer, false);
    }

    public Column addRendererColumn(String attributeName, String header, Renderer<T> renderer, boolean caseSensitive)
    {
        return addRendererColumn(attributeName, header, renderer, caseSensitive, SortType.ALPHA);
    }

    public Column addRendererColumn(String attributeName, String header, Renderer<T> renderer, boolean caseSensitive, SortType sortType)
    {
        return addRendererColumn(attributeName, header, renderer, Filter.getDefaultFilterFunction(s -> "%" + s + "%", caseSensitive), Filter.getDefaultSortFunction(), sortType);
    }


    public Column addRendererColumn(String attributeName, String header, Renderer<T> renderer, ValueProvider<String, String> patternProvider)
    {
        return addRendererColumn(attributeName, header, renderer, patternProvider, false);
    }

    public Column addRendererColumn(String attributeName, String header, Renderer<T> renderer, ValueProvider<String, String> patternProvider, boolean caseSensitive)
    {
        return addRendererColumn(attributeName, header, renderer, patternProvider, caseSensitive, SortType.ALPHA);
    }

    public Column addRendererColumn(String attributeName, String header, Renderer<T> renderer, ValueProvider<String, String> patternProvider, boolean caseSensitive, SortType sortType)
    {
        return addRendererColumn(attributeName, header, renderer, Filter.getDefaultFilterFunction(patternProvider, caseSensitive), Filter.getDefaultSortFunction(), sortType);
    }

    public Column addRendererColumn(String attributeName, String header, Renderer<T> renderer, Filter.FilterFunction<T> filterFunction)
    {
        return addRendererColumn(attributeName, header, renderer, filterFunction, SortType.ALPHA);
    }

    public Column addRendererColumn(String attributeName, String header, Renderer<T> renderer, Filter.FilterFunction<T> filterFunction, SortType sortType)
    {
        return addRendererColumn(attributeName, header, renderer, filterFunction, Filter.getDefaultSortFunction(), sortType);
    }

    public Column addRendererColumn(String attributeName, String header, Renderer<T> renderer, Filter.FilterFunction<T> filterFunction, Filter.SortFunction<T> sortFunction, SortType sortType)
    {
        Filter<T> filter = new Filter<>(attributeName, filterFunction, sortFunction);
        addFilter(filter);

        //Wenn es fehler im AutoFilterGrid gibt dann den folgenden Code Block

        //hier
        if(sortColumn == null)
            dataProvider.applyFilters();
        else
            dataProvider.applyFilters(sortColumn.getAttributeName(), sortColumn.getSortOrder());

        //bis hier

        //nach unten packen

        Column column = new Column(attributeName, sortType, header, renderer, s ->
        {
            filter.setFilterInput(s);

            dataProvider.refreshAll();
            //und das hier entfernen
        });

        columns.add(column);

        setActiveSortColumn(columns.get(0), SortOrder.ASCENDING);

        return column;
    }



//    public Column addColumn(String attributeName, String header, ValueProvider<T, Component> componentProvider, Filter.FilterFunction<T> filterFunction, Filter.SortFunction<T> sortFunction, SortType sortType)
//    {
//        Filter<T> filter = new Filter<>(attributeName, filterFunction, sortFunction);
//        addFilter(filter);
//
//        Column column = new Column(attributeName, sortType, header, componentProvider, s ->
//        {
//            filter.setFilterInput(s);
//            if(sortColumn == null)
//                dataProvider.callFilters();
//            else
//                dataProvider.callFilters(sortColumn.getAttributeName(), sortColumn.getSortOrder());
//        });
//
//        columns.add(column);
//
//        setActiveSortColumn(columns.get(0), SortOrder.ASCENDING);
//
//        return column;
//    }



    @Override
    public <V extends Component> Grid.Column<T> addComponentColumn(ValueProvider<T, V> componentProvider)
    {
        return addComponentColumn(null, componentProvider);
    }

    public <V extends Component> Grid.Column<T> addComponentColumn(String header, ValueProvider<T, V> componentProvider)
    {
        Grid.Column<T> column = super.addComponentColumn(componentProvider).setFlexGrow(0).setAutoWidth(true);

        NativeLabel label = new NativeLabel(header);
        label.getStyle()
                .set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-m)");

        TextField textField = new TextField();
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
        private SortOrder sortOrder = SortOrder.UNSORTED;
        private SortType sortType;
        private boolean enableSorting = true;

        private final String attributeName;
        private final String header;
        private final ValueProvider<T, String> valueProvider;
        private final ValueProvider<T, Component> componentProvider;
        private final Renderer<T> renderer;
        private final Consumer<String> onFilterChangeConsumer;

        private Grid.Column<T> gridColumn;

        private NativeLabel label;
        private HorizontalLayout inputLayout;
        private TextField inputField;
        private Button sortButton;

        public Column(String attributeName, SortType sortType, String header, ValueProvider<T, String> valueProvider, Consumer<String> onFilterChangeConsumer)
        {
            this.sortType = sortType;

            this.attributeName = attributeName;
            this.header = header;
            this.valueProvider = valueProvider;
            this.componentProvider = null;
            this.renderer = null;
            this.onFilterChangeConsumer = onFilterChangeConsumer;

            construct();
        }

        public Column(String attributeName, SortType sortType, String header, Consumer<String> onFilterChangeConsumer, ValueProvider<T, Component> componentProvider)
        {
            this.sortType = sortType;

            this.attributeName = attributeName;
            this.header = header;
            this.valueProvider = null;
            this.componentProvider = componentProvider;
            this.renderer = null;
            this.onFilterChangeConsumer = onFilterChangeConsumer;

            construct();
        }

        public Column(String attributeName, SortType sortType, String header,Renderer<T> renderer, Consumer<String> onFilterChangeConsumer)
        {
            this.sortType = sortType;

            this.attributeName = attributeName;
            this.header = header;
            this.valueProvider = null;
            this.componentProvider = null;
            this.renderer = renderer;
            this.onFilterChangeConsumer = onFilterChangeConsumer;

            construct();
        }

        private void construct()
        {
            if(valueProvider != null)
            {
                gridColumn = AutofilterGrid.this.addColumn(valueProvider);

            }else if(componentProvider != null)
            {
                gridColumn = AutofilterGrid.this.addComponentColumn(componentProvider);

            }else if(renderer != null)
            {
                gridColumn = AutofilterGrid.this.addColumn(renderer);
            }


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
            inputField.setValueChangeMode(ValueChangeMode.LAZY);
            inputField.setClearButtonVisible(true);
            inputField.setWidth("100%");
            inputField.setMinWidth("1%");
            inputField.addValueChangeListener(event ->
            {
                onFilterChangeConsumer.accept(event.getValue());
            });


            sortButton = new Button(LineAwesomeIcon.SORT_SOLID.create());
            sortButton.addClickListener(event ->
            {
                AutofilterGrid.this.sortColumn = this;

                setSortOrder(nextSortOrder());

                dataProvider.applyFilters(attributeName, sortOrder);

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

        dataProvider.applyFilters(sortColumn.getAttributeName(), sortColumn.getSortOrder());

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
