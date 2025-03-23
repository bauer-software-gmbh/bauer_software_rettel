package de.bauersoft.components.autofilter.grid;

import org.vaadin.lineawesome.LineAwesomeIcon;

import javax.swing.SortOrder;

public enum SortType
{
    ALPHA(LineAwesomeIcon.SORT_ALPHA_UP_SOLID, LineAwesomeIcon.SORT_ALPHA_DOWN_SOLID),
    AMOUNT(LineAwesomeIcon.SORT_AMOUNT_UP_SOLID, LineAwesomeIcon.SORT_AMOUNT_DOWN_SOLID),
    NUMERIC(LineAwesomeIcon.SORT_NUMERIC_UP_SOLID, LineAwesomeIcon.SORT_NUMERIC_DOWN_SOLID),
    DEFAULT(LineAwesomeIcon.SORT_UP_SOLID, LineAwesomeIcon.SORT_DOWN_SOLID);

    private LineAwesomeIcon up;
    private LineAwesomeIcon down;

    private SortType(LineAwesomeIcon up, LineAwesomeIcon down)
    {
        this.up = up;
        this.down = down;
    }

    public LineAwesomeIcon get(SortOrder sortOrder)
    {
        switch(sortOrder)
        {
            case ASCENDING:
                return up;

            case DESCENDING:
                return down;

            default:
                return LineAwesomeIcon.SORT_SOLID;
        }
    }

    public LineAwesomeIcon getUp()
    {
        return up;
    }

    public LineAwesomeIcon getDown()
    {
        return down;
    }
}
