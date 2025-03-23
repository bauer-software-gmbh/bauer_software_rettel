package de.bauersoft.data.entities.role;

public enum Role
{
    ADMIN("Admin"),
    KITCHEN("Kitchen"),
    KITCHEN_ADMIN("Kitchen Admin"),
    OFFICE("Office"),
    OFFICE_ADMIN("Office Admin"),
    INSTITUTION("Institution"),
    ORDER_SHOW_ALL_INSTITUTIONS("Order Show All Institutions"),
    ORDER_TIME_BYPASS("Order Time Bypass"),
    CLOSING_TIMES("Closing Times"),
    CLOSING_TIMES_SHOW_ALL_INSTITUTIONS("Closing Times Show All Institutions"),
    ;

    private String name;

    Role(String name)
    {
        this.name = name;
    }
}
