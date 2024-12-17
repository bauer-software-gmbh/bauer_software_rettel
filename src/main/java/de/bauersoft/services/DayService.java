package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.Day;
import de.bauersoft.data.entities.Menu;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.day.DayGridDataRepository;
import de.bauersoft.data.repositories.day.DayRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class DayService {

    private final DayRepository repository;
    private final DayGridDataRepository customRepository;

    public DayService(DayRepository repository, DayGridDataRepository customRepository) {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    public Day saveDay(Day day) {
        return repository.save(day);
    }

    public Optional<Day> findById(Long id) {
        return repository.findById(id);
    }

    public List<Day> findAll() {
        return repository.findAll();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public void addMenuToDay(Long dayId, Menu menu) {
        Day day = repository.findById(dayId).orElseThrow(() -> new RuntimeException("Tag nicht gefunden"));
        day.getMenus().add(menu);
        repository.save(day);
    }

    @Transactional
    public Optional<Day> findByDate(LocalDate date) {
        return repository.findByDate(date);
    }

    public int count(List<SerializableFilter<Day, ?>> filter) {
        return (int) customRepository.count(filter);
    }

    public List<Day> fetchAll(List<SerializableFilter<Day, ?>> filters, List<QuerySortOrder> sortOrder) {
        return customRepository.fetchAll(filters,sortOrder);
    }

    public List<Day> fetchAll(LocalDate startDate, LocalDate endDate) {
        SerializableFilter<Day,LocalDate> startDateFilter = new SerializableFilter<>(){

            private LocalDate value;

            @Override
            public boolean test(Day day) {
                return !day.getDate().isBefore(this.value) && day.getWeek().getYear() == this.value.getYear() && day.getWeek().getKw() == this.value.get((TemporalField) WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
            }

            @Override
            public String getFieldName() {
                return "date";
            }

            @Override
            public void setValue(LocalDate value) {
                this.value = value;
            }

            @Override
            public LocalDate getValue() {
                return this.value;
            }

            @Override
            public void clear() {
                this.value = null;
            }

            @Override
            public Predicate getPredicate(CriteriaBuilder criteriaBuilder, Path<?> externalPath) {
                Path<LocalDate> path = (Path<LocalDate>) externalPath;
                return this.value == null ? null : criteriaBuilder.greaterThanOrEqualTo(path, this.value);
            }
        };
        SerializableFilter<Day,LocalDate> endDateFilter = new SerializableFilter<>(){

            private LocalDate value;

            @Override
            public boolean test(Day day) {
                return !day.getDate().isAfter(this.value) && day.getWeek().getYear() == this.value.getYear() && day.getWeek().getKw() == this.value.get((TemporalField) WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
            }

            @Override
            public String getFieldName() {
                return "date";
            }

            @Override
            public void setValue(LocalDate value) {
                this.value = value;
            }

            @Override
            public LocalDate getValue() {
                return this.value;
            }

            @Override
            public void clear() {
                this.value = null;
            }

            @Override
            public Predicate getPredicate(CriteriaBuilder criteriaBuilder, Path<?> externalPath) {
                Path<LocalDate> path = (Path<LocalDate>) externalPath;
                return this.value == null ? null : criteriaBuilder.lessThanOrEqualTo(path, this.value);
            }
        };
        startDateFilter.setValue(startDate);
        endDateFilter.setValue(endDate);
        return fetchAll(Arrays.asList(startDateFilter, endDateFilter), List.of());
    }

    public List<Menu> getMenusByDayId(Long dayId) {
        Optional<Day> dayOptional = repository.findById(dayId);
        if (dayOptional.isPresent()) {
            return dayOptional.get().getMenus();
        }
        return List.of(); // Gibt eine leere Liste zurück, falls der Day nicht existiert
    }
}

