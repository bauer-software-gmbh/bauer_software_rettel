package de.bauersoft.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;

import de.bauersoft.data.entities.Institution;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.institution.InstitutionRepository;
import de.bauersoft.data.repositories.field.InstitutionFieldsRepository;
import de.bauersoft.data.repositories.institution.InstitutionGridDataRepository;
@Service
public class InstitutionService {
    private final InstitutionRepository repository;
    private final InstitutionGridDataRepository customRepository;
    private final InstitutionFieldsRepository institutionFieldsRepository;
    
    public InstitutionService(InstitutionRepository repository,InstitutionGridDataRepository customRepository,InstitutionFieldsRepository institutionFieldsRepository) {
        this.repository = repository;
        this.customRepository = customRepository;
        this.institutionFieldsRepository = institutionFieldsRepository;
    }

    public Optional<Institution> get(Long id) {
        return repository.findById(id);
    }

    public Institution update(Institution entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Institution> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Institution> list(Pageable pageable, Specification<Institution> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
    
    public int count (List<SerializableFilter<Institution, ?>> filters){
    	return (int) customRepository.count(filters);
    }
    
    public List<Institution> fetchAll(List<SerializableFilter<Institution, ?>> filters, List<QuerySortOrder> sortOrder){
    	List<Institution> result = customRepository.fetchAll(filters,sortOrder);
		result.forEach(item-> item.setFields(institutionFieldsRepository.findAllByInstitutionId(item.getId())));
		return result;
    }

}
