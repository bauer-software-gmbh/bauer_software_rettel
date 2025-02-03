package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.address.Address;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.address.AddressGridDataRepository;
import de.bauersoft.data.repositories.address.AddressRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService implements ServiceBase<Address, Long>
{
    private final AddressRepository repository;
    private final AddressGridDataRepository customRepository;

    public AddressService(AddressRepository repository, AddressGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<Address> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Address update(Address entity)
    {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public Page<Address> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Address> list(Pageable pageable, Specification<Address> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Address, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<Address> fetchAll(List<SerializableFilter<Address, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public AddressRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Address> getCustomRepository()
    {
        return customRepository;
    }
}
