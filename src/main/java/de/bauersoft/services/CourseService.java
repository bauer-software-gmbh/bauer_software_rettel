package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.course.CourseGridDataRepository;
import de.bauersoft.data.repositories.course.CourseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService implements ServiceBase<Course, Long>
{
    private final CourseRepository repository;
    private final CourseGridDataRepository customRepository;

    public CourseService(CourseRepository repository, CourseGridDataRepository customRepository)
    {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    @Override
    public Optional<Course> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public Course update(Course entity)
    {
        return repository.save(entity);
    }

    @Override
    public List<Course> updateAll(Collection<Course> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(Course entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<Course> entities)
    {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll()
    {
        repository.deleteAll();
    }

    @Override
    public void deleteAllById(Collection<Long> ids)
    {
        repository.deleteAllById(ids);
    }

    @Override
    public List<Course> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Page<Course> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Course> list(Pageable pageable, Specification<Course> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<Course, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<Course> fetchAll(List<SerializableFilter<Course, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public CourseRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<Course> getCustomRepository()
    {
        return customRepository;
    }
}
