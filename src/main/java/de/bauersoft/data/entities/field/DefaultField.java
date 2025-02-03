package de.bauersoft.data.entities.field;

import java.util.Arrays;
import java.util.Optional;

public enum DefaultField
{
    GRUNDSCHULE("Grundschule"),
    SCHULE("Schule"),
    KINDERTAGESSTÄTTE("Kindertagesstätte"),
    KINDERGARTEN("Kindergarten");

    private String name;

    DefaultField(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public Optional<DefaultField> fromName(String name)
    {
        return Optional.ofNullable
                (
                        Arrays.stream(DefaultField.values())
                                .filter(defaultField -> defaultField.name.equals(name))
                                .findFirst()
                                .orElse(null)
                );
    }

    public boolean equalsDefault(Field field)
    {
        return (field == null) ? false : this.name.equals(field.getName());
    }
}
