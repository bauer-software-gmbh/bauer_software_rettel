package de.bauersoft.components.autofilter;

import com.vaadin.flow.function.ValueProvider;
import jakarta.persistence.criteria.*;

public class Filter<T>
{
    private String attributeName;
    private ValueProvider<Root<?>, Path<?>> pathProvider;

    private String filterInput;
    private FilterFunction<T> filterFunction;

    private boolean ignoreFilterInput;

    public Filter(ValueProvider<Root<?>, Path<?>> pathProvider)
    {
        this.pathProvider = pathProvider;

        filterInput = "";
        filterFunction = (root, path, criteriaQuery, criteriaBuilder, parent, input) ->
        {
            return criteriaBuilder.like(path.as(String.class), "%" + input + "%");
        };
    }

    public Filter(ValueProvider<Root<?>, Path<?>> pathProvider, FilterFunction<T> filterFunction)
    {
        this.pathProvider = pathProvider;
        this.filterFunction = filterFunction;

        filterInput = "";
    }

    public Filter(String attributeName)
    {
        this.attributeName = attributeName;

        pathProvider = root -> root.get(attributeName);

        filterInput = "";
        filterFunction = (root, path, criteriaQuery, criteriaBuilder, parent, input) ->
        {
            return criteriaBuilder.like(path.as(String.class), "%" + input + "%");
        };
    }

    public Filter(String attributeName, FilterFunction<T> filterFunction)
    {
        this.attributeName = attributeName;
        pathProvider = root -> root.get(attributeName);
        this.filterFunction = filterFunction;
    }

    public String getAttributeName()
    {
        return attributeName;
    }

    public String getFilterInput()
    {
        return filterInput;
    }

    public Filter<T> setFilterInput(String filterInput)
    {
        this.filterInput = filterInput;
        return this;
    }

    public ValueProvider<Root<?>, Path<?>> getPathProvider()
    {
        return pathProvider;
    }

    public void setPathProvider(ValueProvider<Root<?>, Path<?>> pathProvider)
    {
        this.pathProvider = pathProvider;
    }

    public FilterFunction<T> getFilterFunction()
    {
        return filterFunction;
    }

    public Filter<T> setFilterFunction(FilterFunction<T> filterFunction)
    {
        this.filterFunction = filterFunction;
        return this;
    }

    public boolean isIgnoreFilterInput()
    {
        return ignoreFilterInput;
    }

    public Filter<T> setIgnoreFilterInput(boolean ignoreFilterInput)
    {
        this.ignoreFilterInput = ignoreFilterInput;
        return this;
    }

    public interface FilterFunction<T>
    {
        Predicate apply(Root<T> root, Path<?> path, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder, Predicate parent, String filterInput);
    }
}
