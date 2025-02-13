package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.institution.InstitutionPattern;
import de.bauersoft.data.entities.institution.InstitutionPatternKey;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.institutionPattern.InstitutionPatternGridDataRepository;
import de.bauersoft.data.repositories.institutionPattern.InstitutionPatternRepository;
import de.bauersoft.views.institution.InstitutionView;
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
public class InstitutionPatternService implements ServiceBase<InstitutionPattern, InstitutionPatternKey>
{
    private final InstitutionPatternRepository repository;
    private final InstitutionPatternGridDataRepository customRepository;

    public InstitutionPatternService(InstitutionPatternRepository repository, InstitutionPatternGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<InstitutionPattern> get(InstitutionPatternKey institutionPatternKey)
    {
        return repository.findById(institutionPatternKey);
    }

    @Override
    public InstitutionPattern update(InstitutionPattern entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<InstitutionPattern> updateAll(Collection<InstitutionPattern> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(InstitutionPattern entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(InstitutionPatternKey institutionPatternKey)
    {
        repository.deleteById(institutionPatternKey);
    }

    @Override
    public void deleteAll(Collection<InstitutionPattern> entities)
    {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll()
    {
        repository.deleteAll();
    }

    @Override
    public void deleteAllById(Collection<InstitutionPatternKey> institutionPatternKeys)
    {
        repository.deleteAllById(institutionPatternKeys);
    }

    @Override
    public List<InstitutionPattern> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<InstitutionPattern> list(Pageable pageable)
    {
        return null;
    }

    @Override
    public Page<InstitutionPattern> list(Pageable pageable, Specification<InstitutionPattern> filter)
    {
        return null;
    }

    @Override
    public long count()
    {
        return 0;
    }

    @Override
    public long count(List<SerializableFilter<InstitutionPattern, ?>> serializableFilters)
    {
        return 0;
    }

    @Override
    public List<InstitutionPattern> fetchAll(List<SerializableFilter<InstitutionPattern, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return List.of();
    }

    @Override
    public <E extends JpaRepository<InstitutionPattern, InstitutionPatternKey> & JpaSpecificationExecutor<InstitutionPattern>> E getRepository()
    {
        return null;
    }

    @Override
    public GridDataRepository<InstitutionPattern> getCustomRepository()
    {
        return null;
    }
}
