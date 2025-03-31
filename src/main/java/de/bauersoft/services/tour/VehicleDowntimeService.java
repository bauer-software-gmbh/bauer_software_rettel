package de.bauersoft.services.tour;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.tour.vehicle.VehicleDowntime;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.tour.VehicleDowntimeRepository;
import de.bauersoft.services.ServiceBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleDowntimeService implements ServiceBase<VehicleDowntime, Long>
{
    private final VehicleDowntimeRepository repository;

    public VehicleDowntimeService(VehicleDowntimeRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public Optional<VehicleDowntime> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public VehicleDowntime update(VehicleDowntime entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<VehicleDowntime> updateAll(Collection<VehicleDowntime> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(VehicleDowntime entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<VehicleDowntime> entities)
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
    public List<VehicleDowntime> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<VehicleDowntime> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<VehicleDowntime> list(Pageable pageable, Specification<VehicleDowntime> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<VehicleDowntime, ?>> serializableFilters)
    {
        return 0;
    }

    @Override
    public List<VehicleDowntime> fetchAll(List<SerializableFilter<VehicleDowntime, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return List.of();
    }

    @Override
    public VehicleDowntimeRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<VehicleDowntime> getCustomRepository()
    {
        return null;
    }

    public List<VehicleDowntime> findAllByVehicle_Id(Long vehicleId)
    {
        return repository.findAllByVehicle_Id(vehicleId);
    }

    public Optional<VehicleDowntime> getNextVehicleDowntime(Long vehicleId)
    {
        List<VehicleDowntime> downtimes = repository.findAllByVehicle_Id(vehicleId);
        return downtimes.stream()
                .filter(d -> d.getStartDate().isAfter(LocalDate.now()))
                .sorted(Comparator.comparing(VehicleDowntime::getStartDate))
                .findFirst();
    }
}
