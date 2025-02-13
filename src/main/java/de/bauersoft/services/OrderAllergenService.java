package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.order.OrderAllergen;
import de.bauersoft.data.entities.order.OrderAllergenKey;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.order.OrderAllergenGridDataRepository;
import de.bauersoft.data.repositories.order.OrderAllergenRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class OrderAllergenService implements ServiceBase<OrderAllergen, OrderAllergenKey>
{
    private final OrderAllergenRepository repository;
    private final OrderAllergenGridDataRepository customRepository;

    public OrderAllergenService(OrderAllergenRepository repository, OrderAllergenGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<OrderAllergen> get(OrderAllergenKey orderAllergenKey)
    {
        return repository.findById(orderAllergenKey);
    }

    @Override
    public OrderAllergen update(OrderAllergen entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<OrderAllergen> updateAll(Collection<OrderAllergen> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(OrderAllergen entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(OrderAllergenKey orderAllergenKey)
    {
        repository.deleteById(orderAllergenKey);
    }

    @Override
    public void deleteAll(Collection<OrderAllergen> entities)
    {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll()
    {
        repository.deleteAll();
    }

    @Override
    public void deleteAllById(Collection<OrderAllergenKey> orderAllergenKeys)
    {
        repository.deleteAllById(orderAllergenKeys);
    }

    @Override
    public List<OrderAllergen> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<OrderAllergen> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<OrderAllergen> list(Pageable pageable, Specification<OrderAllergen> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<OrderAllergen, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<OrderAllergen> fetchAll(List<SerializableFilter<OrderAllergen, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public OrderAllergenRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<OrderAllergen> getCustomRepository()
    {
        return customRepository;
    }


    public void deleteAllByOrderId(Long orderId)
    {
        repository.deleteAllByOrderId(orderId);
    }
}
