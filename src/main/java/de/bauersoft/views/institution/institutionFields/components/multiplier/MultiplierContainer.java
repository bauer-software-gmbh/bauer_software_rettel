package de.bauersoft.views.institution.institutionFields.components.multiplier;

import de.bauersoft.data.entities.course.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MultiplierContainer
{
    private Course course;
    private Double multiplier;
}
