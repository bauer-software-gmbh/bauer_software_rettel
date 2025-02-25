package de.bauersoft.data.entities.institutionFieldMultiplier;

import de.bauersoft.data.entities.course.Course;
import de.bauersoft.components.container.ContainerID;
import de.bauersoft.data.entities.institutionField.InstitutionField;
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
public class InstitutionMultiplier implements ContainerID<InstitutionMultiplierKey>
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
    private double multiplier = 1;

    @Override
    public String toString()
    {
        return "InstitutionMultiplier{" +
                "id=" + id +
                ", course=" + course +
                ", multiplier=" + multiplier +
                '}';
    }
}
