package de.bauersoft.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;

import de.bauersoft.data.entities.Unit;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.unit.UnitGridDataRepository;
import de.bauersoft.data.repositories.unit.UnitRepository;

@Service
public class UnitService {

    private final UnitRepository repository;
    private final UnitGridDataRepository customRepository;
   
    public UnitService(UnitRepository repository,UnitGridDataRepository customRepository) {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    public Optional<Unit> get(Long id) {
        return repository.findById(id);
    }

    public Unit update(Unit entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Unit> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Unit> list(Pageable pageable, Specification<Unit> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
    
    public int count (List<SerializableFilter<Unit, ?>> filters){
    	return (int) customRepository.count(filters);
    }
    
    public List<Unit> fetchAll(List<SerializableFilter<Unit, ?>> filters, List<QuerySortOrder> sortOrder){
    	return customRepository.fetchAll(filters,sortOrder);
    }
}
