package de.bauersoft.data.repositories.institutionfields;

import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class InstitutionFieldsGridDataRepository extends AbstractGridDataRepository<InstitutionField>
{
    public InstitutionFieldsGridDataRepository()
    {
        super(InstitutionField.class);
    }
}
