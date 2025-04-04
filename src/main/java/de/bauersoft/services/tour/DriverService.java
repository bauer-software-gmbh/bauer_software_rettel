package de.bauersoft.services.tour;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.tour.driver.Driver;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.tour.DriverRepository;
import de.bauersoft.services.ServiceBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.lang.ScopedValue;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class DriverService implements ServiceBase<Driver, Long>
{
    private final DriverRepository repository;

    public DriverService(DriverRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public Optional<Driver> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Driver update(Driver entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<Driver> updateAll(Collection<Driver> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(Driver entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<Driver> entities)
    {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll()
    {
        repository.deleteAll();
    }

    @Override
    public void deleteAllById(Collection<Long> ids)
    {
        repository.deleteAllById(ids);
    }

    @Override
    public List<Driver> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<Driver> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Driver> list(Pageable pageable, Specification<Driver> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Driver, ?>> serializableFilters)
    {
        return 0;
    }

    @Override
    public List<Driver> fetchAll(List<SerializableFilter<Driver, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return List.of();
    }

    @Override
    public DriverRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Driver> getCustomRepository()
    {
        return null;
    }

    public boolean existsDriverByUser_Id(Long userId)
    {
        return repository.existsDriverByUser_Id(userId);
    }

    public List<Driver> findAllUnplannedAllowedDrivers(Long tourId, boolean holidayMode)
    {
        return repository.findAllUnplannedAllowedDrivers(tourId, holidayMode);
    }

    public List<Driver> findAllUnplannedDrivers(boolean holidayMode)
    {
        return repository.findAllUnplannedDrivers(holidayMode);
    }

    public void deleteAllDriveableToursByTourId(Long tourId)
    {
        repository.deleteAllDriveableToursByTourId(tourId);
    }

    public Optional<Driver> findDriverByUserId(Long userId) {
        return repository.findByUser_Id(userId);
    }
}
