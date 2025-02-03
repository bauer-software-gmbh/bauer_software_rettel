package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.institution.InstitutionMultiplier;
import de.bauersoft.data.entities.institution.InstitutionMultiplierKey;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.institution.InstitutionMultiplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstitutionMultiplierService implements ServiceBase<InstitutionMultiplier, InstitutionMultiplierKey>
{
    private final InstitutionMultiplierRepository repository;

    public InstitutionMultiplierService(InstitutionMultiplierRepository repository)
    {
        this.repository = repository;
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
    public void delete(InstitutionMultiplierKey institutionMultiplierKey)
    {
        repository.deleteById(institutionMultiplierKey);
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
        return 0;
    }

    @Override
    public List<InstitutionMultiplier> fetchAll(List<SerializableFilter<InstitutionMultiplier, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return List.of();
    }

    @Override
    public InstitutionMultiplierRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<InstitutionMultiplier> getCustomRepository()
    {
        return null;
    }

    public void updateInstitutionMultipliers()
    {

    }
}

