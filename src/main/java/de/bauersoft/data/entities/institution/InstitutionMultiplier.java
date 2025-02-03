package de.bauersoft.data.entities.institution;

import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.field.Field;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "institution_multiplier")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class InstitutionMultiplier
{
    @EmbeddedId
    private InstitutionMultiplierKey id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("institutionId")
    @JoinColumn(name = "institution_id")
    private Institution institution;

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

    @Column(nullable = false, columnDefinition = "TINYINT(1) default 0")
    private boolean isLocal;
}
