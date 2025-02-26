package de.bauersoft.data.entities.fieldMultiplier;

import de.bauersoft.components.container.ContainerID;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.field.Field;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "field_multiplier")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FieldMultiplier implements ContainerID<FieldMultiplierKey>
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
    private double multiplier = 1d;
}
