package de.bauersoft.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;

import de.bauersoft.data.entities.Field;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.field.FieldGridDataRepository;
import de.bauersoft.data.repositories.field.FieldRepository;

@Service
public class FieldService {

    private final FieldRepository repository;
    private final FieldGridDataRepository customRepository;
    
    public FieldService(FieldRepository repository,FieldGridDataRepository customRepository) {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    public Optional<Field> get(Long id) {
        return repository.findById(id);
    }

    public Field update(Field entity) {
    	return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Field> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Field> list(Pageable pageable, Specification<Field> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
    
    public int count (List<SerializableFilter<Field, ?>> filters){
    	return (int) customRepository.count(filters);
    }
    
    
    public List<Field> fetchAll(List<SerializableFilter<Field, ?>> filters, List<QuerySortOrder> sortOrder){
    	return customRepository.fetchAll(filters,sortOrder);
    }
}
