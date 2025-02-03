package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.institution.InstitutionGridDataRepository;
import de.bauersoft.data.repositories.institution.InstitutionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstitutionService implements ServiceBase<Institution, Long>
{
    private final InstitutionRepository repository;
    private final InstitutionGridDataRepository customRepository;

    public InstitutionService(InstitutionRepository repository, InstitutionGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<Institution> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Institution update(Institution entity)
    {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public Page<Institution> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Institution> list(Pageable pageable, Specification<Institution> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Institution, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<Institution> fetchAll(List<SerializableFilter<Institution, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public InstitutionRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Institution> getCustomRepository()
    {
        return customRepository;
    }
}
