package de.bauersoft.components.autofiltergrid;

public interface QuadFunctionCopy<T, U, V, X, R>
{
    R apply(T t, U u, V v, X x);
}
