package de.bauersoft.data.entities.flesh;

import java.util.Arrays;
import java.util.Optional;

public enum DefaultFlesh
{
    CHICKEN("Hühnchen"),
    BEEF("Rindfleisch"),
    FISH("Fisch");

    private String name;

    DefaultFlesh(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public Optional<DefaultFlesh> fromName(String name)
    {
        return Optional.ofNullable
                (
                        Arrays.stream(DefaultFlesh.values())
                                .filter(defaultFlesh -> defaultFlesh.name.equals(name))
                                .findFirst()
                                .orElse(null)
                );
    }

    public boolean equalsDefault(Flesh flesh)
    {
        return (flesh == null) ? false : this.name.equals(flesh.getName());
    }
}

