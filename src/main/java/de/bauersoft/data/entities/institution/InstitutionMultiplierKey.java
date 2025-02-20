package de.bauersoft.data.entities.institution;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InstitutionMultiplierKey
{
    @Column(nullable = false)
    private Long institutionFieldId;

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
        return Objects.equals(institutionFieldId, that.institutionFieldId) && Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(institutionFieldId, courseId);
    }

    @Override
    public String toString()
    {
        return "InstitutionMultiplierKey{" +
                "institutionFieldId=" + institutionFieldId +
                ", courseId=" + courseId +
                '}';
    }
}
