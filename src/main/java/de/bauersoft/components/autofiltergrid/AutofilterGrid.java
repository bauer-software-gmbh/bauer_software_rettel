package de.bauersoft.components.autofiltergrid;

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
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AutofilterGrid<T> extends Grid<T>
{
    private JpaSpecificationExecutor<T> specificationExecutor;

    private List<GridFilter<T>> gridFilters;

    private HeaderRow headerRow;

    private final AutofilterGridContextMenu autofilterGridContextMenu;

    private CallbackDataProvider<T, GridFilter<T>> CallbackDataProvider;
    private ConfigurableFilterDataProvider<T, Void, GridFilter<T>> filterDataProvider;

    public AutofilterGrid(JpaSpecificationExecutor<T> specificationExecutor)
    {
        this.specificationExecutor = specificationExecutor;

        gridFilters = new ArrayList<>();

        this.getHeaderRows().clear();
        headerRow = appendHeaderRow();

        autofilterGridContextMenu = new AutofilterGridContextMenu();

        this.CallbackDataProvider = DataProvider.fromFilteringCallbacks(
                query ->
                {
                    Pageable pageable = PageRequest.of(query.getOffset() / query.getLimit(), query.getLimit());
                    return specificationExecutor.findAll(createSpecification(), pageable).stream();
                },
                query -> (int) specificationExecutor.count(createSpecification())
        );

        filterDataProvider = CallbackDataProvider.withConfigurableFilter();
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

    private Specification<T> createSpecification()
    {
        return (root, query, criteriaBuilder) ->
        {
            Predicate predicate = criteriaBuilder.conjunction();
            for(GridFilter<T> gridFilter : gridFilters)
            {
                Path<?> path = root.get(gridFilter.getAttributeName());
                String filterInput = gridFilter.getFilterInput();
                if(filterInput == null || filterInput.isEmpty()) continue;

                predicate = criteriaBuilder.and(predicate, gridFilter.getFilterFunction().apply(root, path, query, criteriaBuilder, predicate, filterInput));
            }

            return predicate;
        };
    }



    public JpaSpecificationExecutor<T> getSpecificationExecutor()
    {
        return specificationExecutor;
    }

    public List<GridFilter<T>> getGridFilters()
    {
        return gridFilters;
    }

    public HeaderRow getHeaderRow()
    {
        return headerRow;
    }

    public AutofilterGridContextMenu getAutofilterGridContextMenu()
    {
        return autofilterGridContextMenu;
    }

    public CallbackDataProvider<T, GridFilter<T>> getCallbackDataProvider()
    {
        return CallbackDataProvider;
    }

    public ConfigurableFilterDataProvider<T, Void, GridFilter<T>> getFilterDataProvider()
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
