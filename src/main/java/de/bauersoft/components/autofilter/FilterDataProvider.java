package de.bauersoft.components.autofilter;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.function.ValueProvider;
import de.bauersoft.services.ServiceBase;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilterDataProvider<T, ID> extends CallbackDataProvider<T, Specification<T>>
{
    private final ConcurrentHashMap<Integer, List<T>> cache;

    private final ConfigurableFilterDataProvider<T, Void, Specification<T>> filterDataProvider;
    private final ServiceBase<T, ID> service;

    private final List<Filter<T>> filters;

    public FilterDataProvider(ServiceBase<T, ID> service)
    {
        super(query ->
        {
            Pageable pageable = PageRequest.of(query.getOffset() / query.getLimit(), query.getLimit());
            Specification<T> filter = query.getFilter().orElse(Specification.where(null));

            return service.getRepository().findAll(filter, pageable).stream();

        }, query ->
        {
            Specification<T> filter = query.getFilter().orElse(Specification.where(null));
            return (int) service.getRepository().count(filter);
        });

        cache = new ConcurrentHashMap<>();

        this.filterDataProvider = this.withConfigurableFilter();

        this.service = service;

        this.filters = new ArrayList<>();
    }

    @Override
    public Stream<T> fetchFromBackEnd(Query<T, Specification<T>> query)
    {
        int page = query.getOffset() / query.getLimit();
        List<T> cachedItems = cache.computeIfAbsent(page, p ->
        {
            List<T> items = super.fetchFromBackEnd(query).collect(Collectors.toList());
            System.out.println("cached");
            return items;
        });

        Specification<T> filter = query.getFilter().orElse(Specification.where(null));
        return null;
    }

    public ConfigurableFilterDataProvider<T, Void, Specification<T>> getFilterDataProvider()
    {
        return filterDataProvider;
    }

    public ServiceBase<T, ID> getService()
    {
        return service;
    }

    public List<Filter<T>> getFilters()
    {
        return filters;
    }

    public FilterDataProvider<T, ID> addFilters(Filter<T>... filters)
    {
        this.filters.addAll(Arrays.stream(filters).toList());

        applyFilters();
        return this;
    }

    public FilterDataProvider<T, ID> addFilter(Filter<T> filter)
    {
        filters.add(filter);

        applyFilters();
        return this;
    }

    public FilterDataProvider<T, ID> removeFilter(Filter<T> filter)
    {
        if(filters.remove(filter))
            applyFilters();

        return this;
    }

    public FilterDataProvider<T, ID> applyFilters()
    {
        return applyFilters(null, SortOrder.UNSORTED);
    }

    public FilterDataProvider<T, ID> applyFilters(String sortAttributeName, SortOrder sortOrder)
    {
        filterDataProvider.setFilter(buildFilter(sortAttributeName, sortOrder));
        filterDataProvider.refreshAll();
        return this;
    }

    public Specification<T> buildFilter()
    {
        return buildFilter(null, SortOrder.UNSORTED);
    }

    public Specification<T> buildFilter(String sortAttributeName, SortOrder sortOrder)
    {
        Objects.requireNonNull(sortOrder);

        return (root, query, criteriaBuilder) ->
        {
            Predicate predicate = criteriaBuilder.conjunction();
            Order order = null;
            for(Filter<T> filter : filters)
            {
                Path<?> path = root.get(filter.getAttributeName());
                String filterInput = filter.getFilterInput();

                if(sortAttributeName != null && filter.getAttributeName().equals(sortAttributeName))
                    order = filter.getSortFunction().apply(root, path, query, criteriaBuilder, predicate, sortOrder);


                if(!filter.ignoreFilterInput() && (filterInput == null || filterInput.isEmpty())) continue;

                predicate = criteriaBuilder.and(predicate, filter.getFilterFunction().apply(root, path, query, criteriaBuilder, predicate, filterInput));
            }

            if(order != null)
                query.orderBy(order);

            return predicate;
        };
    }

    public static <T> Stream<T> lazyStream(ServiceBase<T, ?> service, Query<T, ?> query)
    {
        int offset = query.getOffset();
        int limit = query.getLimit();
        Pageable pageable = PageRequest.of(offset / limit, limit);

        return service.list(pageable).stream();
    }

    public static <T> Stream<T> lazyFilteredStream(ServiceBase<T, ?> service, Query<T, String> query, String attributeName)
    {
        return lazyFilteredStream(service, query, s -> "%" + s + "%", attributeName);
    }

    public static <T> Stream<T> lazyFilteredStream(ServiceBase<T, ?> service, Query<T, String> query, ValueProvider<String, String> patternProvider, String attributeName)
    {
        return lazyFilteredStream(service, query, (root, criteriaQuery, criteriaBuilder, filterInput) ->
        {
            return criteriaBuilder.like(criteriaBuilder.lower(root.get(attributeName)), patternProvider.apply(filterInput));
        });
    }

    public static <T> Stream<T> lazyFilteredStream(ServiceBase<T, ?> service, Query<T, String> query, Filter.TinyFilterFunction<T> tinyFilterFunction)
    {
        int offset = query.getOffset();
        int limit = query.getLimit();
        Pageable pageable = PageRequest.of(offset / limit, limit);

        return service.list(pageable, (root, criteriaQuery, criteriaBuilder) ->
        {
            return tinyFilterFunction.apply(root, criteriaQuery, criteriaBuilder, query.getFilter().orElseGet(() -> ""));
        }).stream();
    }

    public static Pageable pageable(Query<?, ?> query)
    {
        int offset = query.getOffset();
        int limit = query.getLimit();
        return PageRequest.of(offset / limit, limit);
    }


    @Deprecated
    public DataProvider<T, Specification<T>> getDataProvider()
    {
        return DataProvider.fromFilteringCallbacks(
                query ->
                {
                    Pageable pageable = PageRequest.of(query.getOffset() / query.getLimit(), query.getLimit());
                    Specification<T> filter = query.getFilter().orElse(Specification.where(null));

                    return service.getRepository().findAll(filter, pageable).stream();
                },
                query ->
                {
                    Specification<T> filter = query.getFilter().orElse(Specification.where(null));
                    return (int) service.getRepository().count(filter);
                }
        );
    }

}
