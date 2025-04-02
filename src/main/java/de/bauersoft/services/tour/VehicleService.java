package de.bauersoft.services.tour;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.tour.vehicle.Vehicle;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.tour.VehicleRepository;
import de.bauersoft.services.ServiceBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService implements ServiceBase<Vehicle, Long>
{
    private final VehicleRepository repository;

    public VehicleService(VehicleRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public Optional<Vehicle> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Vehicle update(Vehicle entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<Vehicle> updateAll(Collection<Vehicle> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(Vehicle entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<Vehicle> entities)
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
    public List<Vehicle> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<Vehicle> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Vehicle> list(Pageable pageable, Specification<Vehicle> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Vehicle, ?>> serializableFilters)
    {
        return 0;
    }

    @Override
    public List<Vehicle> fetchAll(List<SerializableFilter<Vehicle, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return List.of();
    }

    @Override
    public VehicleRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Vehicle> getCustomRepository()
    {
        return null;
    }

    public List<Vehicle> findAllUnplannedVehicles(boolean holidayMode)
    {
        return repository.findAllUnplannedVehicles(holidayMode);
    }
}
