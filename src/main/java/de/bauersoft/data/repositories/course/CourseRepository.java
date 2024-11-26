package de.bauersoft.data.repositories.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.bauersoft.data.entities.Course;

public interface CourseRepository  extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course>{
}
