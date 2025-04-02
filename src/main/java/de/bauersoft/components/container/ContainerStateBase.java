package de.bauersoft.components.container;

public interface ContainerStateBase
{
    Enum<? extends ContainerStateBase> getEnum();

    boolean isVisible();

    boolean isHidden();

    boolean isIgnored();
}
