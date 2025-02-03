package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.data.entities.institution.InstitutionFieldKey;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.institutionfields.InstitutionFieldsGridDataRepository;
import de.bauersoft.data.repositories.institutionfields.InstitutionFieldsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InstitutionFieldsService implements ServiceBase<InstitutionField, InstitutionFieldKey>
{
    private final InstitutionFieldsRepository repository;
    private final InstitutionFieldsGridDataRepository customRepository;

    public InstitutionFieldsService(InstitutionFieldsRepository repository, InstitutionFieldsGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<InstitutionField> get(InstitutionFieldKey institutionFieldsKey)
    {
        return repository.findById(institutionFieldsKey);
    }

    @Override
    public InstitutionField update(InstitutionField entity)
    {
        return repository.save(entity);
    }

    @Override
    public void delete(InstitutionFieldKey institutionFieldsKey)
    {
        repository.deleteById(institutionFieldsKey);
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

    public void updateInstitutionFields(List<InstitutionField> oldInstitutionFields, List<InstitutionField> newInstitutionFields)
    {
        if(oldInstitutionFields == null)
            oldInstitutionFields = new ArrayList<>();

        if(newInstitutionFields == null)
            newInstitutionFields = new ArrayList<>();

        List<InstitutionField> remove = new ArrayList<>();
        List<InstitutionField> update = new ArrayList<>(newInstitutionFields);

        for(InstitutionField oldInstitutionField : oldInstitutionFields)
        {
            if(!newInstitutionFields.contains(oldInstitutionField))
                remove.add(oldInstitutionField);
        }

        repository.deleteAll(remove);
        repository.saveAll(update);
    }
}
