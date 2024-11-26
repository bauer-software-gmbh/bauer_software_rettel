package de.bauersoft.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;

import de.bauersoft.data.entities.Recipe;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.formulation.FormulationRepository;
import de.bauersoft.data.repositories.recipe.RecipeGridDataRepository;
import de.bauersoft.data.repositories.recipe.RecipeRepository;

@Service
public class RecipeService {
	private final RecipeRepository repository;
	private final RecipeGridDataRepository customRepository;
	private final FormulationRepository formulationRepository;
	
	RecipeService(FormulationRepository formulationRepository,RecipeRepository repository, RecipeGridDataRepository customRepository) {	
		this.formulationRepository = formulationRepository;
		this.repository = repository;
		this.customRepository = customRepository;
	}

	public Optional<Recipe> get(Long id) {
		return repository.findById(id);
	}

	public Recipe update(Recipe entity) {
		
		return repository.save(entity);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}
	
	public Page<Recipe> list(Pageable pageable) {
		return repository.findAll(pageable);
	}

	public Page<Recipe> list(Pageable pageable, Specification<Recipe> filter) {
		return repository.findAll(filter, pageable);
	}

	public int count() {
		return (int) repository.count();
	}

	public int count(List<SerializableFilter<Recipe, ?>> filters) {
		return (int) customRepository.count(filters);
	}


	public List<Recipe> fetchAll(List<SerializableFilter<Recipe, ?>> filters, List<QuerySortOrder> sortOrder) {
		List<Recipe> result = customRepository.fetchAll(filters,sortOrder);
			result.forEach(item-> item.setFormulations(formulationRepository.findAllByRecipeId(item.getId())));
		return result;
	}

	
}