package de.bauersoft.components.autofilter;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import de.bauersoft.services.ServiceBase;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FilterDataProvider<T, ID> extends CallbackDataProvider<T, Specification<T>>
{
    private ConfigurableFilterDataProvider<T, Void, Specification<T>> filterDataProvider;
    private ServiceBase<T, ID> service;

    private List<Filter<T>> filters;

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

        this.filterDataProvider = this.withConfigurableFilter();

        this.service = service;

        this.filters = new ArrayList<>();
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

    public FilterDataProvider<T, ID> addFilter(Filter<T> filter)
    {
        filters.add(filter);

        callFilters();
        return this;
    }

    public FilterDataProvider<T, ID> callFilters()
    {
        filterDataProvider.setFilter(buildFilter());
        filterDataProvider.refreshAll();
        return this;
    }



    private Specification<T> buildFilter()
    {
        return (root, query, criteriaBuilder) ->
        {
            Predicate predicate = criteriaBuilder.conjunction();
            for(Filter<T> filter : filters)
            {
                Path<?> path = root.get(filter.getAttributeName());
                String filterInput = filter.getFilterInput();

                if(!filter.isIgnoreFilterInput() && (filterInput == null || filterInput.isEmpty())) continue;

                predicate = criteriaBuilder.and(predicate, filter.getFilterFunction().apply(root, path, query, criteriaBuilder, predicate, filterInput));
            }

            return predicate;
        };
    }

}
