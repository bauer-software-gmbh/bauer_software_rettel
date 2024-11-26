package de.bauersoft.data.repositories.course;

import org.springframework.stereotype.Service;

import de.bauersoft.data.entities.Course;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;

@Service
public class CourseGridDataRepository extends AbstractGridDataRepository<Course>{

	public CourseGridDataRepository() {
		super(Course.class);
	}

}