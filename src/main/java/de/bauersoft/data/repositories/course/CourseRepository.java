package de.bauersoft.data.repositories.course;

import de.bauersoft.data.entities.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course>
{
}
