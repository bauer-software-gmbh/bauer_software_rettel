package de.bauersoft.services.tour;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.tour.tour.Tour;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.tour.TourRepository;
import de.bauersoft.services.ServiceBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class TourService implements ServiceBase<Tour, Long>
{
    private final TourRepository repository;

    public TourService(TourRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public Optional<Tour> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Tour update(Tour entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<Tour> updateAll(Collection<Tour> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(Tour entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<Tour> entities)
    {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll()
    {
        repository.deleteAll();
    }

    @Override
    public void deleteAllById(Collection<Long> longs)
    {
        repository.deleteAllById(longs);
    }

    @Override
    public List<Tour> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<Tour> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Tour> list(Pageable pageable, Specification<Tour> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Tour, ?>> serializableFilters)
    {
        return 0;
    }

    @Override
    public List<Tour> fetchAll(List<SerializableFilter<Tour, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return List.of();
    }

    @Override
    public TourRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Tour> getCustomRepository()
    {
        return null;
    }
}
