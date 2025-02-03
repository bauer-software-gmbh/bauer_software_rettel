package de.bauersoft.data.repositories.formulation;

import de.bauersoft.data.entities.formulation.Formulation;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class FormulationGridDataRepository extends AbstractGridDataRepository<Formulation>
{
    public FormulationGridDataRepository()
    {
        super(Formulation.class);
    }
}
