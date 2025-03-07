package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import de.bauersoft.services.ServiceBase;
import org.springframework.data.jpa.domain.Specification;

public interface DataProviderBase<T, ID, S extends ServiceBase<T, ID>>
{
    public DataProviderBase<T, ID, S> setFilter(Specification<T> filter);

    CallbackDataProvider<T, Specification<T>> getCallbackDataProvider();

    ConfigurableFilterDataProvider<T, Void, Specification<T>> getConfigurableFilterDataProvider();

    ServiceBase<T, ID> getService();

    public void refreshAll();
}
