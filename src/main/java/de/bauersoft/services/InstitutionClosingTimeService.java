package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.institutionClosingTime.InstitutionClosingTime;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.institutonClosingTimes.InstitutionClosingTimeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class InstitutionClosingTimeService implements ServiceBase<InstitutionClosingTime, Long>
{
    private final InstitutionClosingTimeRepository repository;

    public InstitutionClosingTimeService(InstitutionClosingTimeRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public Optional<InstitutionClosingTime> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public InstitutionClosingTime update(InstitutionClosingTime entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<InstitutionClosingTime> updateAll(Collection<InstitutionClosingTime> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(InstitutionClosingTime entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<InstitutionClosingTime> entities)
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
    public List<InstitutionClosingTime> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<InstitutionClosingTime> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<InstitutionClosingTime> list(Pageable pageable, Specification<InstitutionClosingTime> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<InstitutionClosingTime, ?>> serializableFilters)
    {
        return 0;
    }

    @Override
    public List<InstitutionClosingTime> fetchAll(List<SerializableFilter<InstitutionClosingTime, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return List.of();
    }

    @Override
    public InstitutionClosingTimeRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<InstitutionClosingTime> getCustomRepository()
    {
        return null;
    }
}
