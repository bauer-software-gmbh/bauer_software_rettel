package de.bauersoft.components.container;

public enum ContainerState
{
    SHOW,
    HIDE,
    UPDATE,
    DELETE,
    IGNORE,
    NEW;

    public boolean isVisible()
    {
        return this == SHOW || this == UPDATE;
    }

    public boolean isFunctional()
    {
        return this == UPDATE || this == DELETE;
    }
}
