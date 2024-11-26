package de.bauersoft.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;

import de.bauersoft.data.entities.Allergen;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.allergen.AllergenGridDataRepository;
import de.bauersoft.data.repositories.allergen.AllergenRepository;

@Service
public class AllergenService {

	    private final AllergenRepository repository;
	    private final AllergenGridDataRepository customRepository;
	    
	    public AllergenService(AllergenRepository repository,AllergenGridDataRepository customRepository) {
	        this.repository = repository;
	        this.customRepository = customRepository;
	    }

	    public Optional<Allergen> get(Long id) {
	        return repository.findById(id);
	    }

	    public Allergen update(Allergen entity) {
	    	return repository.save(entity);
	    }

	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    public Page<Allergen> list(Pageable pageable) {
	        return repository.findAll(pageable);
	    }

	    public Page<Allergen> list(Pageable pageable, Specification<Allergen> filter) {
	        return repository.findAll(filter, pageable);
	    }

	    public int count() {
	        return (int) repository.count();
	    }
	    
	    public int count (List<SerializableFilter<Allergen, ?>> filters){
	    	return (int) customRepository.count(filters);
	    }
	    
	    
	    public List<Allergen> fetchAll(List<SerializableFilter<Allergen, ?>> filters, List<QuerySortOrder> sortOrder){
	    	return customRepository.fetchAll(filters,sortOrder);
	    }
	}
