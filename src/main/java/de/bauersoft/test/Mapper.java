package de.bauersoft.test;

import java.util.Objects;
import java.util.Random;

public class Mapper<M>
{
    private M mapper;
    private int fallback;

    public Mapper(M mapper)
    {
        this.mapper = mapper;
        fallback = new Random().nextInt(999) + 1;
    }

    public M getMapper()
    {
        return mapper;
    }

    public void setMapper(M mapper)
    {
        this.mapper = mapper;
    }

    public int getFallback()
    {
        return fallback;
    }

    public void setFallback(int fallback)
    {
        this.fallback = fallback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mapper<?> mapper1 = (Mapper<?>) o;
        return (mapper != null) ? Objects.equals(mapper, mapper1.mapper) : fallback == mapper1.fallback;
    }

    @Override
    public int hashCode() {
        return (mapper != null) ? Objects.hash(mapper) : Integer.hashCode(fallback);
    }

}
