package de.bauersoft.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;

import de.bauersoft.data.entities.Additive;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.additive.AdditiveGridDataRepository;
import de.bauersoft.data.repositories.additive.AdditiveRepository;

@Service
public class AdditiveService {

	    private final AdditiveRepository repository;
	    private final AdditiveGridDataRepository customRepository;
	    
	    public AdditiveService(AdditiveRepository repository,AdditiveGridDataRepository customRepository) {
	        this.repository = repository;
	        this.customRepository = customRepository;
	    }

	    public Optional<Additive> get(Long id) {
	        return repository.findById(id);
	    }

	    public Additive update(Additive entity) {
	    	return repository.save(entity);
	    }

	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    public Page<Additive> list(Pageable pageable) {
	        return repository.findAll(pageable);
	    }

	    public Page<Additive> list(Pageable pageable, Specification<Additive> filter) {
	        return repository.findAll(filter, pageable);
	    }

	    public int count() {
	        return (int) repository.count();
	    }
	    
	    public int count (List<SerializableFilter<Additive, ?>> filters){
	    	return (int) customRepository.count(filters);
	    }
	    
	    public List<Additive> fetchAll(List<SerializableFilter<Additive, ?>> filters, List<QuerySortOrder> sortOrder){
	    	return customRepository.fetchAll(filters,sortOrder);
	    }
	}
