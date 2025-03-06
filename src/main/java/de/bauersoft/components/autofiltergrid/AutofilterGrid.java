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
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AutofilterGrid<T> extends Grid<T>
{
    private JpaSpecificationExecutor<T> specificationExecutor;

    private CallbackDataProvider<T, GridFilter> CallbackDataProvider;
    private ConfigurableFilterDataProvider<T, Void, GridFilter> filterDataProvider;

    private Map<String, Column<T>> columns;

    private GridFilter filter;

    private HeaderRow headerRow;

    private GridContextMenu<T> gridContextMenu;
    private GridMenuItem<T> gridMenuAddItem;
    private GridMenuItem<T> gridMenuRemoveItem;

    public AutofilterGrid(JpaSpecificationExecutor<T> specificationExecutor)
    {
        this.specificationExecutor = specificationExecutor;

        columns = new HashMap<>();
        filter = new GridFilter();

        this.getHeaderRows().clear();
        headerRow = appendHeaderRow();

        this.setColumnRendering(ColumnRendering.LAZY);

        this.CallbackDataProvider = DataProvider.fromFilteringCallbacks(
                query ->
                {
                    GridFilter filter = query.getFilter().orElse(null);

                    Pageable pageable = PageRequest.of(query.getOffset() / query.getLimit(), query.getLimit());
                    Specification<T> specification = (filter == null) ? Specification.where(null) : createSpecification(filter);

                    //Specification<T> userSpec =  (filter == null || filter.getSpecification() == null) ? Specification.where(null) : filter.getSpecification();

                    return specificationExecutor.findAll(specification, pageable).stream();
                },
                query ->
                {
                    GridFilter filter = query.getFilter().orElse(null);
                    Specification<T> specification = (filter == null) ? Specification.where(null) : createSpecification(filter);

                    //Specification<T> userSpec =  (filter == null || filter.getSpecification() == null) ? Specification.where(null) : filter.getSpecification();

                    return (int) specificationExecutor.count(specification);
                }
        );

        filterDataProvider = CallbackDataProvider.withConfigurableFilter();
        filterDataProvider.setFilter(filter);

        this.setDataProvider(filterDataProvider);
    }

    public void refreshAll()
    {
        filterDataProvider.refreshAll();
    }

    public Grid.Column<T> addColumn(String fieldName, String header, ValueProvider<T, String> valueProvider)
    {
        return addColumn(fieldName, header, valueProvider, (s, tRoot, path, criteriaQuery, criteriaBuilder) ->
        {
            return criteriaBuilder.like(path.as(String.class), "%" + s + "%");
        });
    }

    public Grid.Column<T> addColumn(String fieldName, String header, ValueProvider<T, String> valueProvider, QuadFunction<String, Root<T>, Path<?>, CriteriaQuery<?>, CriteriaBuilder, Predicate> converter)
    {
        Grid.Column<T> column = this.addColumn(valueProvider);

        columns.put(fieldName, new Column<>(fieldName, column, valueProvider, converter));
        //filter.getCriteriaMap().put(fieldName, "");
        filter.getFilterInputMap().put(fieldName, "");
        filter.getPredicates().put(fieldName, converter);

        headerRow.getCell(column).setComponent(createFilterHeader(header, value ->
        {
            filter.getFilterInputMap().put(fieldName, value);
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

    public GridContextMenu<T> addGridContextMenu(String addItemLabel,
                                             ComponentEventListener<GridContextMenu.GridContextMenuItemClickEvent<T>> addEvent,
                                             String removeItemLabel,
                                             ComponentEventListener<GridContextMenu.GridContextMenuItemClickEvent<T>> removeEvent)
    {
        this.gridContextMenu = super.addContextMenu();

        gridMenuAddItem = gridContextMenu.addItem(addItemLabel, addEvent);
        gridMenuRemoveItem = gridContextMenu.addItem(removeItemLabel, removeEvent);

        gridContextMenu.addGridContextMenuOpenedListener(event ->
        {
            gridMenuRemoveItem.setVisible(event.getItem().isPresent());
        });

        return gridContextMenu;
    }

    private VerticalLayout createFilterHeader(String header, Consumer<String> onFilterChangeConsumer)
    {
        NativeLabel label = new NativeLabel((header == null || header.isEmpty()) ? " d" : header);
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

//    public Specification<T> createSpecification(GridFilter<T> filter)
//    {
//        return (root, query, criteriaBuilder) ->
//        {
//            Predicate predicate = criteriaBuilder.conjunction();
//            Map<String, Specification<T>> filterCriteria = filter.getSpecificationMap();
//            for(Map.Entry<String, Specification<T>> entry : filterCriteria.entrySet())
//            {
//                String key = entry.getKey();
//                Specification<T> value = entry.getValue();
//
//                predicate = criteriaBuilder.and(value.toPredicate(root, query, criteriaBuilder));
//            }
//
//            return predicate;
//        };
//    }

    public Specification<T> createSpecification(GridFilter filter)
    {
        return (root, query, criteriaBuilder) ->
        {
            Predicate predicate = criteriaBuilder.conjunction();
            Map<String, QuadFunction<String, Root<T>, Path<?>, CriteriaQuery<?>, CriteriaBuilder, Predicate>> predicates = filter.getPredicates();
            Map<String, String> filterInputMap = filter.getFilterInputMap();
            for(Map.Entry<String, QuadFunction<String, Root<T>, Path<?>, CriteriaQuery<?>, CriteriaBuilder, Predicate>> entry : predicates.entrySet())
            {
                String key = entry.getKey();
                QuadFunction<String, Root<T>, Path<?>, CriteriaQuery<?>, CriteriaBuilder, Predicate> value = entry.getValue();

                Path<?> fieldPath = root.get(key);
                predicate = criteriaBuilder.and(predicate, value.apply(filterInputMap.get(key), root, root.get(key), query, criteriaBuilder));


            }

            return predicate;
        };
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    private class Column<T>
    {
        private String fieldName;
        private Grid.Column<T> gridColumn;
        private ValueProvider<T, String> valueProvider;
        private QuadFunction<String, Root<T>, Path<?>, CriteriaQuery<?>, CriteriaBuilder, Predicate> converter;
    }

    public JpaSpecificationExecutor<T> getSpecificationExecutor()
    {
        return specificationExecutor;
    }

    public CallbackDataProvider<T, GridFilter> getCallbackDataProvider()
    {
        return CallbackDataProvider;
    }

    public ConfigurableFilterDataProvider<T, Void, GridFilter> getFilterDataProvider()
    {
        return filterDataProvider;
    }

    public Map<String, Column<T>> getGridColumns()
    {
        return columns;
    }

    public GridFilter getFilter()
    {
        return filter;
    }

    public HeaderRow getHeaderRow()
    {
        return headerRow;
    }

    public GridContextMenu<T> getGridContextMenu()
    {
        return gridContextMenu;
    }

    public GridMenuItem<T> getGridMenuAddItem()
    {
        return gridMenuAddItem;
    }

    public GridMenuItem<T> getGridMenuRemoveItem()
    {
        return gridMenuRemoveItem;
    }
}
