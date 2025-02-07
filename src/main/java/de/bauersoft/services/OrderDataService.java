package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.order.OrderData;
import de.bauersoft.data.entities.order.OrderDataKey;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.order.OrderDataGridDataRepository;
import de.bauersoft.data.repositories.order.OrderDataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderDataService implements ServiceBase<OrderData, OrderDataKey>
{
    private final OrderDataRepository repository;
    private final OrderDataGridDataRepository customRepository;

    public OrderDataService(OrderDataRepository repository, OrderDataGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<OrderData> get(OrderDataKey orderDataKey)
    {
        return repository.findById(orderDataKey);
    }

    @Override
    public OrderData update(OrderData entity)
    {
        return repository.save(entity);
    }

    @Override
    public void delete(OrderDataKey orderDataKey)
    {
        repository.deleteById(orderDataKey);
    }

    @Override
    public Page<OrderData> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<OrderData> list(Pageable pageable, Specification<OrderData> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<OrderData, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<OrderData> fetchAll(List<SerializableFilter<OrderData, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public OrderDataRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<OrderData> getCustomRepository()
    {
        return customRepository;
    }

    public boolean existsByVariantId(Long id)
    {
        return repository.existsByVariantId(id);
    }

    public boolean existsAnyByVariantIds(Collection<Long> ids)
    {
        return ids.stream().anyMatch(this::existsByVariantId);
    }

    public void updateAll(Collection<OrderData> orderData)
    {
        repository.saveAll(orderData);
    }
}
