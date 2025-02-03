package de.bauersoft.data.entities.field;

import de.bauersoft.data.entities.course.Course;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "field_multiplier")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FieldMultiplier
{
    @EmbeddedId
    private FieldMultiplierKey id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("fieldId")
    @JoinColumn(name = "field_id")
    private Field field;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(nullable = false, columnDefinition = "DOUBLE default 1.0")
    private double multiplier;
}
