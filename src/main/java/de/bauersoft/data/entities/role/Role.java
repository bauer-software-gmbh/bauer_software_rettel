package de.bauersoft.data.entities.role;

public enum Role
{
    ADMIN("Admin"),
    KITCHEN("Küche"),
    KITCHEN_ADMIN("Küche Admin"),
    OFFICE("Büro"),
    OFFICE_ADMIN("Büro Admin"),
    INSTITUTION("Institution"),
    ORDER_SHOW_ALL_INSTITUTIONS("Bestellung: Alle Institutionen anzeigen"),
    ORDER_TIME_BYPASS("Bestellung: Keine Bestellzeitbegrenzung"),
    CLOSING_TIMES("Schließzeiten"),
    CLOSING_TIMES_SHOW_ALL_INSTITUTIONS("Schießzeiten: Alle Institutionen anzeigen"),
    ;

    private String name;

    Role(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
