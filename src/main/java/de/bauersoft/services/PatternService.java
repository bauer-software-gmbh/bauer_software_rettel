package de.bauersoft.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;

import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.pattern.PatternGridDataRepository;
import de.bauersoft.data.repositories.pattern.PatternRepository;

@Service
public class PatternService {

    private final PatternRepository repository;
    private final PatternGridDataRepository customRepository;
    
    public PatternService(PatternRepository repository,PatternGridDataRepository customRepository) {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    public Optional<Pattern> get(Long id) {
        return repository.findById(id);
    }

    public Pattern update(Pattern entity) {
    	return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Pattern> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Pattern> list(Pageable pageable, Specification<Pattern> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
    
    public int count (List<SerializableFilter<Pattern, ?>> filters){
    	return (int) customRepository.count(filters);
    }
    
    
    public List<Pattern> fetchAll(List<SerializableFilter<Pattern, ?>> filters, List<QuerySortOrder> sortOrder){
    	return customRepository.fetchAll(filters,sortOrder);
    }
}
