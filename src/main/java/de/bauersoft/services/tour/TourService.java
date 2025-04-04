package de.bauersoft.services.tour;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.address.Address;
import de.bauersoft.data.entities.order.Order;
import de.bauersoft.data.entities.tour.driver.Driver;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.tour.tour.Tour;
import de.bauersoft.data.entities.tour.tour.TourInstitution;
import de.bauersoft.data.entities.tour.tour.TourEntry;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.order.OrderRepository;
import de.bauersoft.mobile.model.DTO.TourDTO;
import de.bauersoft.data.repositories.address.AddressRepository;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.institution.InstitutionRepository;
import de.bauersoft.data.repositories.tour.DriverRepository;
import de.bauersoft.data.repositories.tour.TourInstitutionRepository;
import de.bauersoft.data.repositories.tour.TourRepository;
import de.bauersoft.data.repositories.tour.TourEntryRepository;
import de.bauersoft.services.ServiceBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TourService implements ServiceBase<Tour, Long>
{
    private final TourRepository repository;
    private final TourInstitutionRepository tourInstitutionRepository;
    private final OrderRepository orderRepository;
    private final TourEntryRepository tourEntryRepository;

    public TourService(TourRepository repository, DriverRepository driverRepository,
                       TourInstitutionRepository tourInstitutionRepository,
                       InstitutionRepository institutionRepository, AddressRepository addressRepository, OrderRepository orderRepository,
                       TourEntryRepository tourEntryRepository)
    {
        this.repository = repository;
        this.tourInstitutionRepository = tourInstitutionRepository;
        this.orderRepository = orderRepository;
        this.tourEntryRepository = tourEntryRepository;
    }

    @Override
    public Optional<Tour> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Tour update(Tour entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<Tour> updateAll(Collection<Tour> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(Tour entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<Tour> entities)
    {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll()
    {
        repository.deleteAll();
    }

    @Override
    public void deleteAllById(Collection<Long> longs)
    {
        repository.deleteAllById(longs);
    }

    @Override
    public List<Tour> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<Tour> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Tour> list(Pageable pageable, Specification<Tour> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Tour, ?>> serializableFilters)
    {
        return 0;
    }

    @Override
    public List<Tour> fetchAll(List<SerializableFilter<Tour, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return List.of();
    }

    @Override
    public TourRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Tour> getCustomRepository()
    {
        return null;
    }

    public List<TourDTO> getToursForDriverAndDate(Long userId) {
        List<Tour> tours = repository.findToursByUserId(userId);

        System.out.println("🚀 Anzahl gefundener Touren: " + tours.size());

        List<TourDTO> tourDTOs = new ArrayList<>();

        for (Tour tour : tours) {
            System.out.println("🔹 Tour gefunden: " + tour.getName() + " (ID: " + tour.getId() + ")");

            Driver driver = tour.getDriver();
            Driver coDriver = tour.getCoDriver();

            List<TourInstitution> tourInstitutions = tourInstitutionRepository.findByTourId(tour.getId());
            List<Institution> institutions = tourInstitutions.stream()
                    .map(TourInstitution::getInstitution)
                    .toList();

            Map<Long, TourInstitution> institutionToTIMap = tourInstitutions.stream()
                    .filter(ti -> ti.getInstitution() != null && ti.getInstitution().getId() != null)
                    .collect(Collectors.toMap(
                            ti -> ti.getInstitution().getId(),
                            ti -> ti,
                            (existing, replacement) -> existing // Duplikate ignorieren
                    ));


            // Adressen aus den Institutionen holen
            List<Address> addresses = institutions.stream()
                    .map(Institution::getAddress)
                    .toList();

            List<Order> orders = orderRepository.findByInstitutionIn(institutions);

            // Notizen abrufen
            TourEntry tourEntry = tourEntryRepository.findByTourIdAndDate(tour.getId(), LocalDate.now());

            System.out.println("🏢 Institutionen gefunden: " + institutions.size());
            System.out.println("📍 Adressen gefunden: " + addresses.size());

            tourDTOs.add(new TourDTO(tour, driver, coDriver, institutions, addresses, orders, institutionToTIMap, tourEntry));
        }

        return tourDTOs;
    }

    public void updateStartTourByDriverIdAndTourId(Long driverId, Long tourId, LocalDateTime localDateTime)
    {
        Tour tour = repository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour mit ID " + tourId + " nicht gefunden"));

        if (tour.getDriver() != null && tour.getDriver().getId().equals(driverId)) {
            tour.setStartDateTime(localDateTime);
            repository.save(tour);
        } else {
            throw new RuntimeException("Fahrer mit ID " + driverId + " ist nicht für die Tour mit ID " + tourId + " verantwortlich.");
        }
    }

    public void updateEndTourByDriverIdAndTourId(Long userId, Long tourId, LocalDateTime localDateTime)
    {
        Tour tour = repository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour mit ID " + tourId + " nicht gefunden"));

        if (tour.getDriver() != null && tour.getDriver().getId().equals(userId)) {
            tour.setEndDateTime(localDateTime);
            repository.save(tour);
        } else {
            throw new RuntimeException("Fahrer mit ID " + userId + " ist nicht für die Tour mit ID " + tourId + " verantwortlich.");
        }
    }
}
