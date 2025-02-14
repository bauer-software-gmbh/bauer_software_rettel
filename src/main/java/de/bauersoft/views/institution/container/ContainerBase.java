package de.bauersoft.views.institution.container;

public interface ContainerBase<ID, M>
{
    ID getId();

    M getMapper();
}
