package de.bauersoft.data.entities.institution;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class InstitutionMultiplierKey implements Serializable
{
    @Column(nullable = false)
    private Long institutionId;

    @Column(nullable = false)
    private Long fieldId;

    @Column(nullable = false)
    private Long courseId;

    @Override
    public boolean equals(Object o)
    {
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        InstitutionMultiplierKey that = (InstitutionMultiplierKey) o;
        return Objects.equals(institutionId, that.institutionId) && Objects.equals(fieldId, that.fieldId) && Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(institutionId, fieldId, courseId);
    }

    @Override
    public String toString()
    {
        return "InstitutionMultiplierKey{" +
                "institutionId=" + institutionId +
                ", fieldId=" + fieldId +
                ", courseId=" + courseId +
                '}';
    }
}
