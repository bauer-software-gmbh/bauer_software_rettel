package de.bauersoft.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;

import de.bauersoft.data.entities.Component;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.component.ComponentGridDataRepository;
import de.bauersoft.data.repositories.component.ComponentRepository;

@Service
public class ComponentService {

    private final ComponentRepository repository;
    private final ComponentGridDataRepository customRepository;
    
    public ComponentService(ComponentRepository repository,ComponentGridDataRepository customRepository) {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    public Optional<Component> get(Long id) {
        return repository.findById(id);
    }

    public Component update(Component entity) {
    	return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Component> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Component> list(Pageable pageable, Specification<Component> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
    
    public int count (List<SerializableFilter<Component, ?>> filters){
    	return (int) customRepository.count(filters);
    }
    
    
    public List<Component> fetchAll(List<SerializableFilter<Component, ?>> filters, List<QuerySortOrder> sortOrder){
    	return customRepository.fetchAll(filters,sortOrder);
    }
}
