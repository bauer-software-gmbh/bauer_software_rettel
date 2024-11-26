package de.bauersoft.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;

import de.bauersoft.data.entities.Address;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.address.AddressGridDataRepository;
import de.bauersoft.data.repositories.address.AddressRepository;

@Service
public class AddressService {

	    private final AddressRepository repository;
	    private final AddressGridDataRepository customRepository;
	    
	    public AddressService(AddressRepository repository,AddressGridDataRepository customRepository) {
	        this.repository = repository;
	        this.customRepository = customRepository;
	    }

	    public Optional<Address> get(Long id) {
	        return repository.findById(id);
	    }

	    public Address update(Address entity) {
	    	return repository.save(entity);
	    }

	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    public Page<Address> list(Pageable pageable) {
	        return repository.findAll(pageable);
	    }

	    public Page<Address> list(Pageable pageable, Specification<Address> filter) {
	        return repository.findAll(filter, pageable);
	    }

	    public int count() {
	        return (int) repository.count();
	    }
	    
	    public int count (List<SerializableFilter<Address, ?>> filters){
	    	return (int) customRepository.count(filters);
	    }
	    
	    
	    public List<Address> fetchAll(List<SerializableFilter<Address, ?>> filters, List<QuerySortOrder> sortOrder){
	    	return customRepository.fetchAll(filters,sortOrder);
	    }
	}
