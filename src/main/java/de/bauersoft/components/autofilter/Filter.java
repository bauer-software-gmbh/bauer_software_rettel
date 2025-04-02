package de.bauersoft.components.autofilter;

import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.function.ValueProvider;
import jakarta.persistence.criteria.*;

import javax.swing.SortOrder;

public class Filter<T>
{
    private String attributeName;

    private FilterFunction<T> filterFunction;
    private SortFunction<T> sortFunction;

    private String filterInput;

    private boolean ignoreFilterInput;

    public Filter(String attributeName)
    {
        this.attributeName = attributeName;
        filterFunction = getDefaultFilterFunction(s -> "%" + s + "%", false);
        sortFunction = getDefaultSortFunction();

        filterInput = "";
    }

    public Filter(String attributeName, FilterFunction<T> filterFunction)
    {
        this.attributeName = attributeName;
        this.filterFunction = filterFunction;
        this.sortFunction = getDefaultSortFunction();

        filterInput = "";;
    }

    public Filter(String attributeName, FilterFunction<T> filterFunction, SortFunction<T> sortFunction)
    {
        this.attributeName = attributeName;
        this.filterFunction = filterFunction;
        this.sortFunction = sortFunction;

        filterInput = "";;

    }

    public String getAttributeName()
    {
        return attributeName;
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

    public SortFunction<T> getSortFunction()
    {
        return sortFunction;
    }

    public void setSortFunction(SortFunction<T> sortFunction)
    {
        this.sortFunction = sortFunction;
    }



    public String getFilterInput()
    {
        return filterInput;
    }

    public void setFilterInput(String filterInput)
    {
        this.filterInput = filterInput;
    }



    public boolean ignoreFilterInput()
    {
        return ignoreFilterInput;
    }

    public Filter<T> setIgnoreFilterInput(boolean ignoreFilterInput)
    {
        this.ignoreFilterInput = ignoreFilterInput;
        return this;
    }

    public static FilterFunction getDefaultFilterFunction(ValueProvider<String, String> patternProvider, boolean caseSensitive)
    {
        return (root, path, criteriaQuery, criteriaBuilder, parent, filter) ->
        {
            return (caseSensitive) ?
                    criteriaBuilder.like(path.as(String.class), patternProvider.apply(filter)) :
                    criteriaBuilder.like(criteriaBuilder.lower(path.as(String.class)), patternProvider.apply(filter).toLowerCase());
        };
    }

    public static SortFunction getDefaultSortFunction()
    {
        return (root, path, criteriaQuery, criteriaBuilder, parent, sortOrder) ->
        {
            switch(sortOrder)
            {
                case ASCENDING:
                    return criteriaBuilder.asc(path);
                case DESCENDING:
                    return criteriaBuilder.desc(path);
                default:
                    return null;
            }
        };
    }

    public interface FilterFunction<T>
    {
        Predicate apply(Root<T> root, Path<?> path, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder, Predicate parent, String filterInput);
    }

    public interface TinyFilterFunction<T>
    {
        Predicate apply(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder, String filterInput);
    }

    public interface SortFunction<T>
    {
        Order apply(Root<T> root, Path<?> path, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder, Predicate parent, SortOrder sortOrder);
    }
}
