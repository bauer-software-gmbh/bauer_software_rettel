package de.bauersoft.services.tourPlanning;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.tourPlanning.tour.TourInstitution;
import de.bauersoft.data.entities.tourPlanning.tour.TourInstitutionKey;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.tourPlanning.TourInstitutionRepository;
import de.bauersoft.services.ServiceBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class TourInstitutionService implements ServiceBase<TourInstitution, TourInstitutionKey>
{
    private final TourInstitutionRepository repository;

    public TourInstitutionService(TourInstitutionRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public Optional<TourInstitution> get(TourInstitutionKey tourInstitutionKey)
    {
        return repository.findById(tourInstitutionKey);
    }

    @Override
    public TourInstitution update(TourInstitution entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<TourInstitution> updateAll(Collection<TourInstitution> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(TourInstitution entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(TourInstitutionKey tourInstitutionKey)
    {
        repository.deleteById(tourInstitutionKey);
    }

    @Override
    public void deleteAll(Collection<TourInstitution> entities)
    {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll()
    {
        repository.deleteAll();
    }

    @Override
    public void deleteAllById(Collection<TourInstitutionKey> tourInstitutionKeys)
    {
        repository.deleteAllById(tourInstitutionKeys);
    }

    @Override
    public List<TourInstitution> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<TourInstitution> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<TourInstitution> list(Pageable pageable, Specification<TourInstitution> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<TourInstitution, ?>> serializableFilters)
    {
        return 0;
    }

    @Override
    public List<TourInstitution> fetchAll(List<SerializableFilter<TourInstitution, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return List.of();
    }

    @Override
    public TourInstitutionRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<TourInstitution> getCustomRepository()
    {
        return null;
    }
}
