package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.pattern.PatternGridDataRepository;
import de.bauersoft.data.repositories.pattern.PatternRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatternService implements ServiceBase<Pattern, Long>
{
    private final PatternRepository repository;
    private final PatternGridDataRepository customRepository;

    public PatternService(PatternRepository repository, PatternGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<Pattern> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Pattern update(Pattern entity)
    {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public Page<Pattern> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Pattern> list(Pageable pageable, Specification<Pattern> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Pattern, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<Pattern> fetchAll(List<SerializableFilter<Pattern, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public PatternRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Pattern> getCustomRepository()
    {
        return customRepository;
    }
}
