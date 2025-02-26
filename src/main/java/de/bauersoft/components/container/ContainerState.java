package de.bauersoft.components.container;

public enum ContainerState
{
    SHOW,
    HIDE,
    UPDATE,
    DELETE,
    IGNORE;

    public boolean view()
    {
        return this == SHOW || this == UPDATE;
    }
}
