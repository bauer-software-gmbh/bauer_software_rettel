package de.bauersoft.components.container;

import java.util.*;
import java.util.stream.Collectors;

public class ContainerMapper
{
    private int mapper;

    public ContainerMapper(Set<Integer> excludes)
    {
        mapper = randomMapper(excludes);
    }

    public ContainerMapper(List<? extends ContainerMapper> containerMappers)
    {
        mapper = randomMapper(containerMappers);
    }

    public ContainerMapper(MapContainer mapContainer)
    {
        mapper = randomMapper(mapContainer);
    }

    public int getMapper()
    {
        return mapper;
    }

    public static int randomMapper(MapContainer mapContainer)
    {
        return randomMapper(mapContainer.getContainerMap().keySet().stream().toList());
    }

    public static int randomMapper(List<? extends ContainerMapper> containerMappers)
    {
        return randomMapper(
                containerMappers.stream()
                        .map(ContainerMapper::getMapper)
                        .collect(Collectors.toSet())
        );
    }

    public static int randomMapper(Set<Integer> excludes)
    {
        Random random = new Random();

        int r = 0;
        do
        {
            r = random.nextInt(999) + 1;

        }while(excludes.contains(r));

        return r;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }

        ContainerMapper that = (ContainerMapper) o;
        return mapper == that.mapper;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(mapper);
    }
}
