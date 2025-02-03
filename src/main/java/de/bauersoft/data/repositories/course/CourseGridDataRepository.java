package de.bauersoft.data.repositories.course;

import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.repositories.griddata.AbstractGridDataRepository;
import org.springframework.stereotype.Service;

@Service
public class CourseGridDataRepository extends AbstractGridDataRepository<Course>
{
    public CourseGridDataRepository()
    {
        super(Course.class);
    }
}
