package de.bauersoft.data.entities.pattern;

import de.bauersoft.data.repositories.pattern.PatternRepository;

import java.util.Optional;

public enum DefaultPattern
{
    /**
     * To ensure the consistency of patterns that are used for hard-coded relationships, these patterns are now available as an enum.
     * These enums are automatically written to the database and do not have to be created manually.
     */

    VEGAN("Vegan", "Vegan", 'N'),
    HALAL("Halal", "Halal", 'Y');

    private String name;
    private String description;
    private Character religious;

    private DefaultPattern(String name, String description, Character religious)
    {
        this.name = name;
        this.description = description;
        this.religious = religious;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public Character getReligious()
    {
        return religious;
    }

    public Optional<Pattern> getPattern(PatternRepository patternRepository)
    {
        return patternRepository.findAll().stream()
                .filter(pattern -> !pattern.getName().equals(name))
                .findFirst();
    }
}
