package de.bauersoft.components.autofilter.grid;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.ColumnRendering;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import de.bauersoft.data.providers.DataProviderBase;
import de.bauersoft.services.ServiceBase;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AutofilterGrid<T, ID> extends Grid<T>
{
    private DataProviderBase<T, ID, ServiceBase<T, ID>> dataProvider;
    private ServiceBase<T, ID> service;

    private List<GridFilter<T>> gridFilters;

    private HeaderRow headerRow;

    private final AutofilterGridContextMenu autofilterGridContextMenu;

    private CallbackDataProvider<T, Specification<T>> callbackDataProvider;
    private ConfigurableFilterDataProvider<T, Void, Specification<T>> filterDataProvider;

    public AutofilterGrid(DataProviderBase<T, ID, ServiceBase<T, ID>> dataProvider)
    {
        this(dataProvider, true);
    }

    public AutofilterGrid(DataProviderBase<T, ID, ServiceBase<T, ID>> dataProvider, boolean filteringTroughDataProvider)
    {
        this.dataProvider = dataProvider;
        this.service = dataProvider.getService();

        gridFilters = new ArrayList<>();

        this.getHeaderRows().clear();
        headerRow = appendHeaderRow();

        autofilterGridContextMenu = new AutofilterGridContextMenu();

       if(!filteringTroughDataProvider)
       {
           this.callbackDataProvider = DataProvider.fromFilteringCallbacks(
                   query ->
                   {
                       Pageable pageable = PageRequest.of(query.getOffset() / query.getLimit(), query.getLimit());

                       Specification<T> filter = query.getFilter().orElse(Specification.where(null));

                       return service.getRepository().findAll(filter, pageable).stream();
                   },
                   query ->
                   {
                       Specification<T> filter = query.getFilter().orElse(Specification.where(null));
                       return (int) service.getRepository().count(filter);
                   }
           );

           this.filterDataProvider = this.callbackDataProvider.withConfigurableFilter();

       }else
       {
           this.callbackDataProvider = dataProvider.getCallbackDataProvider();
           this.filterDataProvider = dataProvider.getConfigurableFilterDataProvider();
       }

        this.setDataProvider(filterDataProvider);

        this.setColumnRendering(ColumnRendering.LAZY);
    }

    public void refreshAll()
    {
        filterDataProvider.refreshAll();
    }

    public Grid.Column<T> addColumn(String attributeName, String header, ValueProvider<T, String> valueProvider)
    {
        return addColumn(attributeName, header, valueProvider, (root, path, criteriaQuery, criteriaBuilder, parent, filter) ->
        {
            return criteriaBuilder.like(path.as(String.class), "%" + filter + "%");
        });
    }

    public Grid.Column<T> addColumn(String attributeName, String header, ValueProvider<T, String> valueProvider, GridFilter.GridFilterFunction<T> filterFunction)
    {
        Grid.Column<T> column = this.addColumn(valueProvider);

        GridFilter<T> gridFilter = new GridFilter<>(attributeName);
        gridFilter.setFilterFunction(filterFunction);
        gridFilters.add(gridFilter);

        headerRow.getCell(column).setComponent(createFilterHeader(header, value ->
        {
            gridFilter.setFilterInput(value);

            filterDataProvider.setFilter(createSpecification());

            filterDataProvider.refreshAll();
        }));

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

    public AutofilterGrid<T, ID> addFilter(GridFilter<T> filter)
    {
        this.gridFilters.add(filter);
        filterDataProvider.setFilter(createSpecification());

        return this;
    }

    private Specification<T> createSpecification()
    {
        return (root, query, criteriaBuilder) ->
        {
            Predicate predicate = criteriaBuilder.conjunction();
            for(GridFilter<T> gridFilter : gridFilters)
            {
                Path<?> path = root.get(gridFilter.getAttributeName());
                String filterInput = gridFilter.getFilterInput();
                if(!gridFilter.isIgnoreFilterInput() && (filterInput == null || filterInput.isEmpty())) continue;

                predicate = criteriaBuilder.and(predicate, gridFilter.getFilterFunction().apply(root, path, query, criteriaBuilder, predicate, filterInput));
            }

            return predicate;
        };
    }

    public DataProviderBase<T, ID, ServiceBase<T, ID>> getNDataProvider()
    {
        return dataProvider;
    }

    public ServiceBase<T, ID> getService()
    {
        return service;
    }

    public List<GridFilter<T>> getGridFilters()
    {
        return gridFilters;
    }

    public HeaderRow getHeaderRow()
    {
        return headerRow;
    }

    public CallbackDataProvider<T, Specification<T>> getCallbackDataProvider()
    {
        return callbackDataProvider;
    }

    public ConfigurableFilterDataProvider<T, Void, Specification<T>> getFilterDataProvider()
    {
        return filterDataProvider;
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
