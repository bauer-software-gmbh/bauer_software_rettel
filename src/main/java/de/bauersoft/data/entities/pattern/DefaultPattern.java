package de.bauersoft.data.entities.pattern;

import de.bauersoft.data.repositories.pattern.PatternRepository;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public enum DefaultPattern
{
    DEFAULT("Default", "Default", 'N'),
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
        return Optional.ofNullable(patternRepository.findByName(name));
    }

    public boolean equalsDefault(Pattern pattern)
    {
        return (pattern == null) ? false : name.equals(pattern.getName());
    }

    public static Collection<Pattern> removeDefaultPattern(Collection<Pattern> patterns, DefaultPattern toRemove)
    {
        Objects.requireNonNull(patterns, "patterns cannot be null");
        Objects.requireNonNull(toRemove, "toRemove cannot be null");

        patterns.removeIf(pattern -> pattern.equalsDefault(toRemove));
        return patterns;
    }
}
