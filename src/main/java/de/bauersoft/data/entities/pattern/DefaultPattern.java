package de.bauersoft.data.entities.pattern;

import de.bauersoft.data.repositories.pattern.PatternRepository;

import java.util.Arrays;
import java.util.Optional;

public enum DefaultPattern
{
    DEFAULT("Normal", "Normale Variante", false),
    VEGETARIAN("Vegetarisch", "Vegetarische Variante", false);

    private String name;
    private String description;
    private boolean religious;

    private DefaultPattern(String name, String description, boolean religious)
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

    public boolean getReligious()
    {
        return religious;
    }

    public static boolean hasDefault(Pattern pattern)
    {
        return Arrays.stream(values()).anyMatch(p -> p.getName().equals(pattern.getName()));
    }

    public Optional<DefaultPattern> fromName(String name)
    {
        return Optional.ofNullable
                (
                        Arrays.stream(DefaultPattern.values())
                                .filter(defaultPattern -> defaultPattern.name.equals(name))
                                .findFirst()
                                .orElse(null)
                );
    }

    public boolean equalsDefault(Pattern pattern)
    {
        return (pattern == null) ? false : this.name.equals(pattern.getName());
    }

    public Optional<Pattern> findPattern(PatternRepository patternRepository)
    {
        return Optional.ofNullable(patternRepository.findByName(this.getName()));
    }
}
