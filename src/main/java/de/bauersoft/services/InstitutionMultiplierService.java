package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.entities.institution.InstitutionMultiplier;
import de.bauersoft.data.entities.institution.InstitutionMultiplierKey;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.institutionMultiplier.InstitutionMultiplierGridDataRepository;
import de.bauersoft.data.repositories.institutionMultiplier.InstitutionMultiplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class InstitutionMultiplierService implements ServiceBase<InstitutionMultiplier, InstitutionMultiplierKey>
{
    private final InstitutionMultiplierRepository repository;
    private final InstitutionMultiplierGridDataRepository customRepository;

    public InstitutionMultiplierService(InstitutionMultiplierRepository repository, InstitutionMultiplierGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<InstitutionMultiplier> get(InstitutionMultiplierKey institutionMultiplierKey)
    {
        return repository.findById(institutionMultiplierKey);
    }

    @Override
    public InstitutionMultiplier update(InstitutionMultiplier entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<InstitutionMultiplier> updateAll(Collection<InstitutionMultiplier> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(InstitutionMultiplier entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(InstitutionMultiplierKey institutionMultiplierKey)
    {
        repository.deleteById(institutionMultiplierKey);
    }

    @Override
    public void deleteAll(Collection<InstitutionMultiplier> entities)
    {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll()
    {
        repository.deleteAll();
    }

    @Override
    public void deleteAllById(Collection<InstitutionMultiplierKey> institutionMultiplierKeys)
    {
        repository.deleteAllById(institutionMultiplierKeys);
    }

    @Override
    public List<InstitutionMultiplier> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<InstitutionMultiplier> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<InstitutionMultiplier> list(Pageable pageable, Specification<InstitutionMultiplier> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<InstitutionMultiplier, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<InstitutionMultiplier> fetchAll(List<SerializableFilter<InstitutionMultiplier, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public InstitutionMultiplierRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<InstitutionMultiplier> getCustomRepository()
    {
        return customRepository;
    }

}

