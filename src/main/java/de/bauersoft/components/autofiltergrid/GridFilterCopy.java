package de.bauersoft.components.autofiltergrid;

import java.util.HashMap;
import java.util.Map;

public class GridFilterCopy
{
    private Map<String, String> criteriaMap;

    public GridFilterCopy()
    {
        criteriaMap = new HashMap<>();
    }

    public Map<String, String> getCriteriaMap()
    {
        return criteriaMap;
    }

    public GridFilterCopy setCriteriaMap(Map<String, String> criteriaMap)
    {
        this.criteriaMap = criteriaMap;
        return this;
    }
}
