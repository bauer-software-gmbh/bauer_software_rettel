package de.bauersoft.services.offer;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.offer.Offer;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.offer.OfferGridDataRepository;
import de.bauersoft.data.repositories.offer.OfferRepository;
import de.bauersoft.services.ServiceBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OfferService implements ServiceBase<Offer, Long>
{
    private final OfferRepository repository;
    private final OfferGridDataRepository customRepository;

    public OfferService(OfferRepository repository, OfferGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<Offer> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Offer update(Offer entity)
    {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public Page<Offer> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Offer> list(Pageable pageable, Specification<Offer> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Offer, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<Offer> fetchAll(List<SerializableFilter<Offer, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return null;
    }

    @Override
    public OfferRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Offer> getCustomRepository()
    {
        return customRepository;
    }

    public Optional<Offer> getByLocalDateAndField(LocalDate date, Field value)
    {
        return repository.findByLocalDateAndField(date, value);
    }

    public void removeMenuFromOffer(Long offerId, Long menuId)
    {
        repository.deleteByIdAndMenusId(offerId, menuId);
    }

    public List<Offer> getOffersBetweenDates(LocalDate startDate, LocalDate endDate, Long fielId)
    {
        return null;
    }

    public Optional<Offer> findByLocalDateAndField(LocalDate date, Field field)
    {
        return repository.findByLocalDateAndField(date, field);
    }

    public boolean existsByMenusId(Long menuId)
    {
        return repository.existsByMenusId(menuId);
    }

    public boolean existsByField(Field field)
    {
        return repository.existsByField(field);
    }
}
