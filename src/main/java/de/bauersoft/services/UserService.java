package de.bauersoft.services;

import de.bauersoft.data.entities.User;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.user.UserGridDataRepository;
import de.bauersoft.data.repositories.user.UserRepository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;

@Service
public class UserService {

    private final UserRepository repository;
    private final UserGridDataRepository customRepository;
    
    public UserService(UserRepository repository,UserGridDataRepository customRepository) {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    public Optional<User> get(Long id) {
        return repository.findById(id);
    }

    public User update(User entity) {
    		return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<User> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<User> list(Pageable pageable, Specification<User> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
    
    public int count (List<SerializableFilter<User, ?>> filters){
    	return (int) customRepository.count(filters);
    }
    
    public List<User> fetchAll(List<SerializableFilter<User, ?>> filters, List<QuerySortOrder> sortOrder){
    	return customRepository.fetchAll(filters,sortOrder);
    }
}
