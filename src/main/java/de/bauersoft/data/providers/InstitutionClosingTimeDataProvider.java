package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import de.bauersoft.data.entities.institutionClosingTime.InstitutionClosingTime;
import de.bauersoft.services.InstitutionClosingTimeService;
import de.bauersoft.services.ServiceBase;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class InstitutionClosingTimeDataProvider
        extends CallbackDataProvider<InstitutionClosingTime, Specification<InstitutionClosingTime>>
        implements DataProviderBase<InstitutionClosingTime, Long, ServiceBase<InstitutionClosingTime, Long>>
{
    private ConfigurableFilterDataProvider<InstitutionClosingTime, Void, Specification<InstitutionClosingTime>> configurableFilterDataProvider;
    private InstitutionClosingTimeService service;

    public InstitutionClosingTimeDataProvider(InstitutionClosingTimeService service)
    {
        super(query ->
        {
            Pageable pageable = PageRequest.of(query.getOffset() / query.getLimit(), query.getLimit());

            Specification<InstitutionClosingTime> filter = query.getFilter().orElse(Specification.where(null));

            return service.getRepository().findAll(filter, pageable).stream();

        }, query ->
        {
            Specification<InstitutionClosingTime> filter = query.getFilter().orElse(Specification.where(null));
            return (int) service.getRepository().count(filter);
        });

        this.configurableFilterDataProvider = this.withConfigurableFilter();

        this.service = service;
    }

    @Override
    public InstitutionClosingTimeDataProvider setFilter(Specification<InstitutionClosingTime> filter)
    {
        configurableFilterDataProvider.setFilter(filter);
        return this;
    }

    @Override
    public InstitutionClosingTimeDataProvider getCallbackDataProvider()
    {
        return this;
    }

    @Override
    public ConfigurableFilterDataProvider<InstitutionClosingTime, Void, Specification<InstitutionClosingTime>> getConfigurableFilterDataProvider()
    {
        return configurableFilterDataProvider;
    }

    @Override
    public InstitutionClosingTimeService getService()
    {
        return service;
    }
}
