package de.bauersoft.components.autofiltergrid;

import jakarta.persistence.criteria.*;

import java.util.HashMap;
import java.util.Map;

public class GridFilter<T>
{
    private Map<String,  QuadFunction<String, Root<T>, Path<?>, CriteriaQuery<?>, CriteriaBuilder, Predicate>> predicates;
    private Map<String, String> filterInputMap;

    public GridFilter()
    {
        predicates = new HashMap<>();
        filterInputMap = new HashMap<>();
    }

    public Map<String,  QuadFunction<String, Root<T>, Path<?>, CriteriaQuery<?>, CriteriaBuilder, Predicate>> getPredicates()
    {
        return predicates;
    }

    public GridFilter<T> setPredicates(Map<String,  QuadFunction<String, Root<T>, Path<?>, CriteriaQuery<?>, CriteriaBuilder, Predicate>> predicates)
    {
        this.predicates = predicates;
        return this;
    }

    public Map<String, String> getFilterInputMap()
    {
        return filterInputMap;
    }

    public GridFilter<T> setFilterInputMap(Map<String, String> filterInputMap)
    {
        this.filterInputMap = filterInputMap;
        return this;
    }
}
