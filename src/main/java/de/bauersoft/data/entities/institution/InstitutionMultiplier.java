package de.bauersoft.data.entities.institution;

import de.bauersoft.data.entities.course.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "institution_multiplier")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InstitutionMultiplier
{
    @EmbeddedId
    private InstitutionMultiplierKey id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("institutionFieldId")
    @JoinColumn(name = "institution_field_id", referencedColumnName = "id")
    private InstitutionField institutionField;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("courseId")
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;

    @Column(nullable = false, columnDefinition = "double default 1.0")
    private double multiplier;

    private boolean isLocal;

}
