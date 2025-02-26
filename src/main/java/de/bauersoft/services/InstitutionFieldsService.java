package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.institutionfields.InstitutionFieldsGridDataRepository;
import de.bauersoft.data.repositories.institutionfields.InstitutionFieldsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class InstitutionFieldsService implements ServiceBase<InstitutionField, Long>
{
    private final InstitutionFieldsRepository repository;
    private final InstitutionFieldsGridDataRepository customRepository;

    public InstitutionFieldsService(InstitutionFieldsRepository repository, InstitutionFieldsGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<InstitutionField> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public InstitutionField update(InstitutionField entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<InstitutionField> updateAll(Collection<InstitutionField> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(InstitutionField entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<InstitutionField> entities)
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
    public List<InstitutionField> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<InstitutionField> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<InstitutionField> list(Pageable pageable, Specification<InstitutionField> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<InstitutionField, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<InstitutionField> fetchAll(List<SerializableFilter<InstitutionField, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public InstitutionFieldsRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<InstitutionField> getCustomRepository()
    {
        return customRepository;
    }

    public Optional<InstitutionField> findByInstitutionAndField(Institution institution, Field field)
    {
        return repository.findByInstitutionAndField(institution, field);
    }
}
