package de.bauersoft.data.repositories.offer;

import de.bauersoft.data.entities.offer.Offer;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class OfferGridDataRepository extends AbstractGridDataRepository<Offer>
{
    public OfferGridDataRepository()
    {
        super(Offer.class);
    }
}
