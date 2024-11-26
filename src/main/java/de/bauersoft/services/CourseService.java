package de.bauersoft.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;

import de.bauersoft.data.entities.Course;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.course.CourseGridDataRepository;
import de.bauersoft.data.repositories.course.CourseRepository;

@Service
public class CourseService {

    private final CourseRepository repository;
    private final CourseGridDataRepository customRepository;
    
    public CourseService(CourseRepository repository,CourseGridDataRepository customRepository) {
        this.repository = repository;
        this.customRepository = customRepository;
    }

    public Optional<Course> get(Long id) {
        return repository.findById(id);
    }

    public Course update(Course entity) {
    	return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Course> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Course> list(Pageable pageable, Specification<Course> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
    
    public int count (List<SerializableFilter<Course, ?>> filters){
    	return (int) customRepository.count(filters);
    }
    
    
    public List<Course> fetchAll(List<SerializableFilter<Course, ?>> filters, List<QuerySortOrder> sortOrder){
    	return customRepository.fetchAll(filters,sortOrder);
    }
}
