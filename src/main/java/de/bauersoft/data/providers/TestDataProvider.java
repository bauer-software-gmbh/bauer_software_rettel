package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import de.bauersoft.data.entities.unit.Unit;
import de.bauersoft.services.ServiceBase;
import de.bauersoft.services.UnitService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class TestDataProvider implements DataProviderBase<Unit, Long, ServiceBase<Unit, Long>>
{
    @Override
    public DataProviderBase<Unit, Long, ServiceBase<Unit, Long>> setFilter(Specification<Unit> filter)
    {
        return null;
    }

    @Override
    public CallbackDataProvider<Unit, Specification<Unit>> getCallbackDataProvider()
    {
        return null;
    }

    @Override
    public ConfigurableFilterDataProvider<Unit, Void, Specification<Unit>> getConfigurableFilterDataProvider()
    {
        return null;
    }

    @Override
    public ServiceBase<Unit, Long> getService()
    {
        return null;
    }

    @Override
    public void refreshAll()
    {

    }
}
