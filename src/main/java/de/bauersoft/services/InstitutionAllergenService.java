package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergen;
import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergenKey;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.institutionAllergen.InstitutionAllergenGridDataRepository;
import de.bauersoft.data.repositories.institutionAllergen.InstitutionAllergenRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class InstitutionAllergenService implements ServiceBase<InstitutionAllergen, InstitutionAllergenKey>
{
    private InstitutionAllergenRepository repository;
    private InstitutionAllergenGridDataRepository customRepository;

    public InstitutionAllergenService(InstitutionAllergenRepository repository, InstitutionAllergenGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<InstitutionAllergen> get(InstitutionAllergenKey institutionAllergenKey)
    {
        return repository.findById(institutionAllergenKey);
    }

    @Override
    public InstitutionAllergen update(InstitutionAllergen entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<InstitutionAllergen> updateAll(Collection<InstitutionAllergen> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(InstitutionAllergen entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(InstitutionAllergenKey institutionAllergenKey)
    {
        repository.deleteById(institutionAllergenKey.getInstitutionFieldId(), institutionAllergenKey.getAllergenId());
    }

    @Override
    public void deleteAll(Collection<InstitutionAllergen> entities)
    {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll()
    {
        repository.deleteAll();
    }

    @Override
    public void deleteAllById(Collection<InstitutionAllergenKey> institutionAllergenKeys)
    {
        repository.deleteAllById(institutionAllergenKeys);
    }

    @Override
    public List<InstitutionAllergen> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<InstitutionAllergen> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<InstitutionAllergen> list(Pageable pageable, Specification<InstitutionAllergen> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<InstitutionAllergen, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<InstitutionAllergen> fetchAll(List<SerializableFilter<InstitutionAllergen, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public InstitutionAllergenRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<InstitutionAllergen> getCustomRepository()
    {
        return customRepository;
    }

    public void deleteAllByInstitutionFieldId(Long institutionFieldId)
    {
        repository.deleteAllByInstitutionFieldId(institutionFieldId);
    }

    public List<InstitutionAllergen> findAllByInstitutionField(InstitutionField institutionField)
    {
        return repository.findAllByInstitutionField(institutionField);
    }
}
