package de.bauersoft.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;

import de.bauersoft.data.entities.Ingredient;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.ingredient.IngredientGridDataRepository;
import de.bauersoft.data.repositories.ingredient.IngredientRepository;

@Service
public class IngredientService {

    private final IngredientRepository repository;
    private final IngredientGridDataRepository customRepository;
    
    public IngredientService(IngredientRepository repository,IngredientGridDataRepository customRepository) {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    public Optional<Ingredient> get(Long id) {
        return repository.findById(id);
    }

    public Ingredient update(Ingredient entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Ingredient> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Ingredient> list(Pageable pageable, Specification<Ingredient> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
    
    public int count (List<SerializableFilter<Ingredient, ?>> filters){
    	return (int) customRepository.count(filters);
    }
    
    public List<Ingredient> fetchAll(List<SerializableFilter<Ingredient, ?>> filters, List<QuerySortOrder> sortOrder){
    	return customRepository.fetchAll(filters,sortOrder);
    }

}
