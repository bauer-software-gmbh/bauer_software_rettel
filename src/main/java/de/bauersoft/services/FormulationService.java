package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.entities.formulation.Formulation;
import de.bauersoft.data.entities.formulation.FormulationKey;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.formulation.FormulationGridDataRepository;
import de.bauersoft.data.repositories.formulation.FormulationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class FormulationService implements ServiceBase<Formulation, FormulationKey>
{
    private final FormulationRepository repository;
    private final FormulationGridDataRepository customRepository;

    public FormulationService(FormulationRepository repository, FormulationGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<Formulation> get(FormulationKey formulationKey)
    {
        return repository.findById(formulationKey);
    }

    @Override
    public Formulation update(Formulation entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<Formulation> updateAll(Collection<Formulation> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(Formulation entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(FormulationKey formulationKey)
    {
        repository.deleteById(formulationKey);
    }

    @Override
    public void deleteAll(Collection<Formulation> entities)
    {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll()
    {
        repository.deleteAll();
    }

    @Override
    public void deleteAllById(Collection<FormulationKey> formulationKeys)
    {
        repository.deleteAllById(formulationKeys);
    }

    @Override
    public List<Formulation> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<Formulation> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Formulation> list(Pageable pageable, Specification<Formulation> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Formulation, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<Formulation> fetchAll(List<SerializableFilter<Formulation, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public FormulationRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Formulation> getCustomRepository()
    {
        return customRepository;
    }

    public void updateFormulations(List<Formulation> oldFormulations, List<Formulation> newFormulations)
    {
        if(oldFormulations == null)
            oldFormulations = new ArrayList<>();

        if(newFormulations == null)
            newFormulations = new ArrayList<>();

        List<Formulation> remove = new ArrayList<>();
        List<Formulation> update = new ArrayList<>(newFormulations);

        for(Formulation oldFormulation : oldFormulations)
        {
            if(!newFormulations.contains(oldFormulation))
                remove.add(oldFormulation);
        }

        repository.deleteAll(remove);
        repository.saveAll(update);
    }

}
