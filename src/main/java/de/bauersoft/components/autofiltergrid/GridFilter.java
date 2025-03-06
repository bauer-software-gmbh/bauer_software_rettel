package de.bauersoft.components.autofiltergrid;

import jakarta.persistence.criteria.*;

import java.util.HashMap;
import java.util.Map;

public class GridFilter<T>
{
    private final String attributeName;

    private String filterInput;
    private GridFilterFunction<T> filterFunction;

    public GridFilter(String attributeName)
    {
        this.attributeName = attributeName;

        filterInput = "";
        filterFunction = (root, path, criteriaQuery, criteriaBuilder, parent, filter) ->
        {
            return criteriaBuilder.like(path.as(String.class), "%" + filter + "%");
        };
    }

    public String getAttributeName()
    {
        return attributeName;
    }

    public GridFilterFunction<T> getFilterFunction()
    {
        return filterFunction;
    }

    public GridFilter<T> setFilterFunction(GridFilterFunction<T> filterFunction)
    {
        this.filterFunction = filterFunction;
        return this;
    }

    public String getFilterInput()
    {
        return filterInput;
    }

    public GridFilter<T> setFilterInput(String filterInput)
    {
        this.filterInput = filterInput;
        return this;
    }

    public interface GridFilterFunction<T>
    {
        Predicate apply(Root<T> root, Path<?> path, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder, Predicate parent, String filterInput);
    }
}
