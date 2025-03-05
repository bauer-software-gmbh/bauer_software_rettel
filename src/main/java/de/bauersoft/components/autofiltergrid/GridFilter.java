package de.bauersoft.components.autofiltergrid;

import java.util.HashMap;
import java.util.Map;

public class GridFilter
{
    private Map<String, String> criteriaMap;

    public GridFilter()
    {
        criteriaMap = new HashMap<>();
    }

    public Map<String, String> getCriteriaMap()
    {
        return criteriaMap;
    }

    public GridFilter setCriteriaMap(Map<String, String> criteriaMap)
    {
        this.criteriaMap = criteriaMap;
        return this;
    }
}
