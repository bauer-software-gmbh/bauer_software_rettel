package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.*;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.services.AdditiveService;
import de.bauersoft.services.ServiceBase;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class AdditiveDataProvider extends FilterDataProvider<Additive, Long>
{
	public AdditiveDataProvider(AdditiveService additiveService)
	{
		super(additiveService);
	}
}