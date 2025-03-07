package de.bauersoft.components.autofilter;

import jakarta.persistence.criteria.*;

public class Filter<T>
{
    private final String attributeName;

    private String filterInput;
    private FilterFunction<T> filterFunction;

    private boolean ignoreFilterInput;

    public Filter(String attributeName)
    {
        this.attributeName = attributeName;

        filterInput = "";
        filterFunction = (root, path, criteriaQuery, criteriaBuilder, parent, input) ->
        {
            return criteriaBuilder.like(path.as(String.class), "%" + input + "%");
        };
    }

    public Filter(String attributeName, FilterFunction<T> filterFunction)
    {
        this.attributeName = attributeName;
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
