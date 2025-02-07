package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.order.Order;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.order.OrderGridDataRepository;
import de.bauersoft.data.repositories.order.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService implements ServiceBase<Order, Long>
{
    private final OrderRepository repository;
    private final OrderGridDataRepository customRepository;

    public OrderService(OrderRepository repository, OrderGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<Order> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Order update(Order entity)
    {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public Page<Order> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Order> list(Pageable pageable, Specification<Order> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Order, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<Order> fetchAll(List<SerializableFilter<Order, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public OrderRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Order> getCustomRepository()
    {
        return customRepository;
    }

    public Optional<Order> findByLocalDateAndInstitutionAndField(LocalDate localDate, Institution institution, Field field)
    {
        return repository.findByLocalDateAndInstitutionAndField(localDate, institution, field);
    }

    public void updateAll(Collection<Order> orders)
    {
        repository.saveAll(orders);
    }
}
